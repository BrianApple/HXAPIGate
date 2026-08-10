package hx.apigate.distributed;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson2.JSON;

import hx.apigate.Entrance;
import hx.apigate.base.IProcessor;
import hx.apigate.distributedCache.config.DistributedCachingProperties;
import hx.apigate.distributedCache.config.DistributedCachingProperties.RedisNodes;
import hx.apigate.distributedCache.config.PropertiesFactory;
import hx.apigate.util.RedisUtil;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;
import redis.clients.jedis.JedisPubSub;

/**
 * Redis 处理器（替代原 IgniteProcessor）
 *
 * 职责（替代 Ignite + ZK 全部职责）：
 *  1. 连接 Redis（复用 DistributeCacheInfo.xml 配置）
 *  2. 启动时全量加载路由缓存 / 鉴权规则到本地（RedisUtil）
 *  3. 订阅路由变更频道（Pub/Sub），收到通知立即重新加载
 *  4. 30s 轮询兜底（防止 Pub/Sub 消息丢失导致网关缓存不一致）
 *
 * 注意：单机模式（application.isCluster=false）不启动 Redis，网关以本地空缓存 + 本地限流运行。
 *
 * @author Hermes (Redis 替代 Ignite/ZK 改造)
 */
public class RedisProcessor implements IProcessor {

    private static final Logger LOGGER = LoggerFactory.getLogger(RedisProcessor.class);

    private JedisPool jedisPool;
    private Jedis subscriber;
    private volatile boolean running = false;
    private ScheduledExecutorService scheduler;

    @Override
    public void start() throws Exception {
        if (!Entrance.isCluster) {
            RedisUtil.setRedisAvailable(false);
            LOGGER.info("单机模式，跳过 Redis 分布式缓存初始化");
            return;
        }
        DistributedCachingProperties props = PropertiesFactory.getDistributedCachingPropertiesInstance();
        RedisNodes node = props.getRedis_host();
        JedisPoolConfig config = new JedisPoolConfig();
        config.setMaxIdle(props.getRedis_maxIdle());
        config.setMaxTotal(props.getRedis_maxTotal());
        config.setMaxWaitMillis(props.getRedis_maxWaitMillis());
        config.setTestOnBorrow(props.isRedis_testOnBorrow());
        config.setTestOnReturn(props.isRedis_testOnReturn());

        if ("OFF".equals(props.getRedis_password())) {
            jedisPool = new JedisPool(config, node.getIp(), node.getPort(), props.getRedis_timeout());
        } else {
            jedisPool = new JedisPool(config, node.getIp(), node.getPort(), props.getRedis_timeout(), props.getRedis_password());
        }
        // 连通性测试
        try (Jedis jedis = jedisPool.getResource()) {
            jedis.ping();
            RedisUtil.setRedisAvailable(true);
        } catch (Exception e) {
            RedisUtil.setRedisAvailable(false);
            LOGGER.warn("Redis 连接失败，网关降级为本地缓存+本地限流: {}", e.getMessage());
            return;
        }

        // 启动时全量加载
        RedisUtil.reloadRouteCache();
        loadAuthCache();

        running = true;
        // Pub/Sub 订阅线程
        Thread subscribeThread = new Thread(this::subscribeLoop, "hxapi-redis-subscriber");
        subscribeThread.setDaemon(true);
        subscribeThread.start();

        // 30s 轮询兜底
        scheduler = Executors.newSingleThreadScheduledExecutor(r -> {
            Thread t = new Thread(r, "hxapi-redis-poll");
            t.setDaemon(true);
            return t;
        });
        scheduler.scheduleAtFixedRate(this::pollReload, 30, 30, TimeUnit.SECONDS);
        LOGGER.info("RedisProcessor started, node={}:{}", node.getIp(), node.getPort());
    }

    /**
     * 从 Redis Hash 全量加载鉴权规则到本地
     */
    private void loadAuthCache() {
        try (Jedis jedis = jedisPool.getResource()) {
            Map<String, String> map = new HashMap<>();
            Map<String, String> all = jedis.hgetAll(RedisUtil.APIAUTH_CACHE_KEY);
            if (all != null) {
                map.putAll(all);
            }
            RedisUtil.setAuthCache(map);
            LOGGER.info("鉴权规则已从 Redis 加载, 共 {} 条", map.size());
        } catch (Exception e) {
            LOGGER.warn("鉴权规则加载失败: {}", e.getMessage());
        }
    }

    /**
     * 订阅路由变更频道（阻塞，独立线程运行）
     */
    private void subscribeLoop() {
        while (running) {
            Jedis jedis = null;
            try {
                jedis = jedisPool.getResource();
                subscriber = jedis;
                jedis.subscribe(new JedisPubSub() {
                    @Override
                    public void onMessage(String channel, String message) {
                        try {
                            InnerMsg msg = JSON.parseObject(message, InnerMsg.class);
                            LOGGER.info("收到路由变更通知: type={}, uri={}", msg.getType(), msg.getUriPattern());
                            // 全量重新加载，简单可靠
                            RedisUtil.reloadRouteCache();
                            loadAuthCache();
                        } catch (Exception e) {
                            LOGGER.warn("处理路由变更通知失败: {}", e.getMessage());
                        }
                    }
                }, RedisUtil.ROUTE_CHANNEL);
            } catch (Exception e) {
                if (running) {
                    LOGGER.warn("Redis 订阅断开，3s 后重连: {}", e.getMessage());
                    try {
                        Thread.sleep(3000);
                    } catch (InterruptedException ie) {
                        Thread.currentThread().interrupt();
                        break;
                    }
                }
            } finally {
                if (jedis != null) {
                    try {
                        jedis.close();
                    } catch (Exception ignore) {
                    }
                }
            }
        }
    }

    /**
     * 30s 轮询兜底：全量重新加载（RedisUtil 内部会整体替换本地 Map 引用，无锁安全）
     */
    private void pollReload() {
        if (!running) {
            return;
        }
        RedisUtil.reloadRouteCache();
        loadAuthCache();
    }

    @Override
    public void stop() throws Exception {
        running = false;
        if (subscriber != null) {
            try {
                subscriber.close();
            } catch (Exception ignore) {
            }
        }
        if (scheduler != null) {
            scheduler.shutdownNow();
        }
        if (jedisPool != null) {
            jedisPool.close();
        }
        LOGGER.info("RedisProcessor stopped");
    }

    @Override
    public int getStartOrder() {
        return 4;
    }

    @Override
    public int getStopOrder() {
        return 4;
    }
}
