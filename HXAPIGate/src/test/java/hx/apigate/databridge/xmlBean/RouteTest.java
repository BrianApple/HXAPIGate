package hx.apigate.databridge.xmlBean;

import hx.apigate.util.RouteSelectUtil;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

/**
 * Route 负载均衡策略单元测试：
 *  - 平滑加权轮询（nextNodeByWeight）权重分布
 *  - 轮询（nextNodeByCircle）循环顺序
 *  - 权重边界（<1 视为 1，>10 视为 10）
 */
public class RouteTest {

    private RouteNode node(String ip, int port, int weight, int tps) {
        RouteNode n = new RouteNode();
        n.setIp(ip);
        n.setPort(port);
        n.setWeight(weight);
        n.setIntTps(tps);
        return n;
    }

    private Route routeWithNodes(List<RouteNode> nodes, String stratege) {
        Route route = new Route();
        route.setMatchUrl("test/route");
        route.setVersion("v1.0.0");
        route.setStratege(stratege);
        route.setProtocal("http");
        route.setNeedAuth(false);
        route.setAllTps(100);
        route.setRouteNodes(nodes);
        route.init();
        return route;
    }

    @Test
    public void testWeightedRoundRobinDistribution() {
        // 权重 2:1，选择 30 次应大致按 2:1 分布
        List<RouteNode> nodes = new ArrayList<>();
        nodes.add(node("10.0.0.1", 8081, 2, 50));
        nodes.add(node("10.0.0.2", 8082, 1, 50));
        Route route = routeWithNodes(nodes, RouteSelectUtil.WEIGHT);

        int countA = 0, countB = 0;
        for (int i = 0; i < 30; i++) {
            RouteNode n = route.nextNodeByWeight();
            if ("10.0.0.1".equals(n.getIp())) countA++;
            else countB++;
        }
        assertEquals("高权重节点应被选中 20 次（30*2/3）", 20, countA);
        assertEquals("低权重节点应被选中 10 次（30*1/3）", 10, countB);
    }

    @Test
    public void testCircleRoundRobinSequence() {
        // 轮询策略：3 个 route 循环（RouteAll 版本级轮询）
        List<Route> routes = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            List<RouteNode> nodes = new ArrayList<>();
            nodes.add(node("10.0.0." + (i + 1), 8081, 1, 50));
            Route r = routeWithNodes(nodes, "circle");
            r.setVersion("v1.0." + i);
            routes.add(r);
        }
        RouteAll all = new RouteAll(false, "test/route==POST");
        for (Route r : routes) {
            all.addInfo(r);
        }

        String[] expected = {"v1.0.0", "v1.0.1", "v1.0.2", "v1.0.0", "v1.0.1", "v1.0.2"};
        for (int i = 0; i < expected.length; i++) {
            Route r = all.nextRoute();
            assertEquals("第 " + (i + 1) + " 次应轮询到 " + expected[i], expected[i], r.getVersion());
        }
    }

    @Test
    public void testWeightBoundaryClamped() {
        // 权重 0 -> 1；权重 100 -> 10；权重 5 -> 5
        List<RouteNode> nodes = new ArrayList<>();
        nodes.add(node("10.0.0.1", 8081, 0, 50));
        nodes.add(node("10.0.0.2", 8082, 100, 50));
        nodes.add(node("10.0.0.3", 8083, 5, 50));
        Route route = routeWithNodes(nodes, WEIGHT_VALUE);

        // 总权重 = 1 + 10 + 5 = 16，选择 16 次，每个节点恰好出现 weight 次（平滑加权轮询特性）
        int c1 = 0, c2 = 0, c3 = 0;
        for (int i = 0; i < 16; i++) {
            RouteNode n = route.nextNodeByWeight();
            if ("10.0.0.1".equals(n.getIp())) c1++;
            else if ("10.0.0.2".equals(n.getIp())) c2++;
            else c3++;
        }
        assertEquals("权重 0 被钳制为 1", 1, c1);
        assertEquals("权重 100 被钳制为 10", 10, c2);
        assertEquals("权重 5 保持 5", 5, c3);
    }

    @Test
    public void testCircuitBreakerFieldsDefaultZero() {
        List<RouteNode> nodes = new ArrayList<>();
        nodes.add(node("10.0.0.1", 8081, 1, 50));
        Route route = routeWithNodes(nodes, WEIGHT_VALUE);
        assertEquals("熔断失败阈值默认 0（自动推导）", 0, route.getCbFailThreshold());
        assertEquals("熔断成功阈值默认 0（自动推导）", 0, route.getCbSuccessThreshold());
        assertEquals("熔断超时默认 0（自动推导）", 0, route.getCbTimeout());

        route.setCbFailThreshold(5);
        route.setCbSuccessThreshold(3);
        route.setCbTimeout(2000);
        assertEquals(5, route.getCbFailThreshold());
        assertEquals(3, route.getCbSuccessThreshold());
        assertEquals(2000, route.getCbTimeout());
    }

    private static final String WEIGHT_VALUE = "weight";
}
