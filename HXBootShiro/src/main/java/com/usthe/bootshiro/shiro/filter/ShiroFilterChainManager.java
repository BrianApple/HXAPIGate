package com.usthe.bootshiro.shiro.filter;


import com.usthe.bootshiro.domain.bo.AuthResource;
import com.usthe.bootshiro.redis.ApiAuthCacheService;
import com.usthe.bootshiro.redis.RedisConstance;
import com.usthe.bootshiro.redis.RouteCacheService;
import com.usthe.bootshiro.service.AccountService;
import com.usthe.bootshiro.service.ResourceService;
import com.usthe.bootshiro.shiro.config.RestPathMatchingFilterChainResolver;
import com.usthe.bootshiro.shiro.provider.ShiroFilterRulesProvider;
import com.usthe.bootshiro.shiro.rule.RolePermRule;
import com.usthe.bootshiro.support.SpringContextHolder;

import hx.apigate.databridge.xmlBean.Route;

import org.apache.shiro.spring.web.ShiroFilterFactoryBean;
import org.apache.shiro.web.filter.mgt.DefaultFilterChainManager;
import org.apache.shiro.web.servlet.AbstractShiroFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;
import jakarta.servlet.Filter;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 *  Filter 管理器
 * @author tomsun28
 * @date 11:16 2018/2/28
 */
@Component
public class ShiroFilterChainManager {
	@Autowired
	private RouteCacheService routeCacheService;
	@Autowired
	private ApiAuthCacheService apiAuthCacheService;
	@Autowired
    private ResourceService resourceService;
	
    private static final Logger LOGGER = LoggerFactory.getLogger(ShiroFilterChainManager.class);

    private final ShiroFilterRulesProvider shiroFilterRulesProvider;
    private final StringRedisTemplate redisTemplate;
    private final AccountService accountService;

    /**
         * 将三个属性在当前类加载容器同时注入进来
     * @param shiroFilterRulesProvider
     * @param redisTemplate
     * @param accountService
     */
    @Autowired
    public ShiroFilterChainManager(ShiroFilterRulesProvider shiroFilterRulesProvider,StringRedisTemplate redisTemplate,AccountService accountService){
        this.shiroFilterRulesProvider = shiroFilterRulesProvider;
        this.redisTemplate = redisTemplate;
        this.accountService = accountService;
        
    }

    /**
     * description 初始化获取过滤链
     * 配置自定义的多个过滤器，具体使用哪个过滤器，是在initGetFilterChain()方法中实现配置的
     * 例： "/css/**","/js/**"配置的过滤器是"anon"，即不需要做任何过滤
     * @return java.util.Map<java.lang.String,jakarta.servlet.Filter>
     */
    public Map<String,Filter> initGetFilters() {
        Map<String,Filter> filters = new LinkedHashMap<>();
        
        PasswordFilter passwordFilter = new PasswordFilter();
        filters.put("auth",passwordFilter);
        
        BonJwtFilter jwtFilter = new BonJwtFilter();
        jwtFilter.setRouteCacheService(routeCacheService);
        jwtFilter.setAccountService(accountService);
        filters.put("jwt",jwtFilter);
        
        initAllApiRoute();//初始化数据库中所有现有API的路由信息
        return filters;
    }
    /**
     * description 初始化获取过滤链规则--项目启动时会执行
     *  将需要拦截的路径以及对应使用的过滤器统一配置好——当前需要加载的仅限bootshiro自己需要鉴权的url，
     * @return java.util.Map<java.lang.String,java.lang.String>
     */
    public Map<String,String> initGetFilterChain() {
        Map<String,String> filterChain = new LinkedHashMap<>();
        // -------------anon 默认过滤器忽略的URL
        List<String> defalutAnon = Arrays.asList("/css/**","/js/**","static/css/**");
        defalutAnon.forEach(ignored -> filterChain.put(ignored,"anon"));
        
        // -------------auth 默认需要认证过滤器的URL 走auth--PasswordFilter
        //登录和注册都是走这个过滤器
        List<String> defalutAuth = Arrays.asList("/inner/user/**");
        defalutAuth.forEach(auth -> filterChain.put(auth,"auth"));
        
        // -------------dynamic 动态URL
        if (shiroFilterRulesProvider != null) {
            /**
             * 从数据库中获取所有角色和资源（API路径和请求类型 ）的对应关系
             * 实例
             * url= "/account/login==POST"
             * needRoles = "role_admin,role_user,role_guest,role_application_uiotcp_portal,role_anon"
             */
            List<RolePermRule> rolePermRules = this.shiroFilterRulesProvider.loadRolePermRules();
            if (null != rolePermRules) {
                rolePermRules.forEach(rule -> {
                    /**
                     * 生成满足shiro规则的过滤器链
                     */
                    StringBuilder chain = rule.toFilterChain4jwt();
                    if (null != chain) {
                    	if(rule.getUrl().startsWith("/inner")) {
                    		if(!rule.getUrl().startsWith("/inner/user")){
								//授权平台自有接口(登录除外) 加入鉴权链路
								filterChain.putIfAbsent(rule.getUrl(),chain.toString());
								System.out.println("Console平台内部鉴权规则==="+rule.getUrl()+"==="+chain.toString());
								LOGGER.info("Console平台内部鉴权规则==="+rule.getUrl()+"==="+chain.toString());
							}

                    	}else {
							//redis缓存需要被代理的API信息
							apiAuthCacheService.put(RedisConstance.API_RESOURCE_ROLE+rule.getUrl(), rule.getNeedRoles());
							System.out.println("HXAPIGate接口鉴权规则==="+rule.getUrl()+"==="+rule.getNeedRoles());
							LOGGER.info("HXAPIGate接口鉴权规则==="+rule.getUrl()+"==="+rule.getNeedRoles());
						}

                    }
                });
            }
        }
        // -------------fallback 兜底：未匹配的URL放行（HXAPIGate对外接口鉴权由网关侧完成，bootshiro仅拦截/inner内部接口）
        // Shiro 3.0+ 对未匹配URL默认应用NoAccessFilter拒绝访问，其saveRequestAndRedirectToLogin需要创建Session，
        // 与本项目无状态设计(StatelessWebSubjectFactory+禁用Session)冲突，导致DisabledSessionException 500。
        // 恢复旧版Shiro(1.x)行为：未匹配URL直接放行。注意必须放在最后，保证/inner等规则优先匹配。
        filterChain.put("/**","anon");
        return filterChain;
    }
    /**
     * description 当前只针对"/inner"内部使用Api，重新加载平台过滤器链
     * @return java.util.Map<java.lang.String,java.lang.String>
     */
    public Map<String,String> updateGetFilterChain() {
    	Map<String,String> filterChain = new LinkedHashMap<>();
    	// -------------anon 默认过滤器忽略的URL
    	List<String> defalutAnon = Arrays.asList("/css/**","/js/**");
    	defalutAnon.forEach(ignored -> filterChain.put(ignored,"anon"));
    	
    	// -------------auth 默认需要认证过滤器的URL 走auth--PasswordFilter
    	//登录和注册都是走这个过滤器
    	List<String> defalutAuth = Arrays.asList("/inner/user/**");
    	defalutAuth.forEach(auth -> filterChain.put(auth,"auth"));
    	
    	// -------------dynamic 动态URL
    	if (shiroFilterRulesProvider != null) {
    		List<RolePermRule> rolePermRules = this.shiroFilterRulesProvider.loadRolePermRules();
    		if (null != rolePermRules) {
    			rolePermRules.forEach(rule -> {
    				/**
    				 * 生成满足shiro规则的过滤器链
    				 */
    				StringBuilder chain = rule.toFilterChain4jwt();
    				if (null != chain) {
    					if(rule.getUrl().startsWith("/inner/") && !rule.getUrl().startsWith("/inner/user")) {
							//授权平台自有接口 加入鉴权链路
    						filterChain.putIfAbsent(rule.getUrl(),chain.toString());
    						System.out.println("Console平台内部鉴权规则更新==="+rule.getUrl()+"==="+chain.toString());
    					}
    				}
    			});
    		}
    	}
    	// -------------fallback 兜底：与initGetFilterChain保持一致，未匹配URL放行(Shiro 3.0 noAccess默认拒绝与无状态冲突)
    	filterChain.put("/**","anon");
    	return filterChain;
    }
    /**
     * description 重新加载过滤链规则（清空缓存中原过滤器链）
     * 当角色授权的资源动态更改之后 需要重新加载过滤器链规则
     */
    public void reloadFilterChain(int apiSourceId) {
    	RolePermRule rule = this.shiroFilterRulesProvider.loadRolePermRulesByResourceId(apiSourceId);
    	String url = rule.getUrl();
    	if(rule.getUrl().startsWith("/inner/")) {
    		//授权平台自有接口 加入鉴权链路
    		ShiroFilterFactoryBean shiroFilterFactoryBean = SpringContextHolder.getBean(ShiroFilterFactoryBean.class);
    		AbstractShiroFilter abstractShiroFilter = null;
    		try {
    			abstractShiroFilter = (AbstractShiroFilter)shiroFilterFactoryBean.getObject();
    			RestPathMatchingFilterChainResolver filterChainResolver = (RestPathMatchingFilterChainResolver)abstractShiroFilter.getFilterChainResolver();
    			DefaultFilterChainManager filterChainManager = (DefaultFilterChainManager)filterChainResolver.getFilterChainManager();
    			//清空原过滤器链缓存
    			filterChainManager.getFilterChains().clear();
    			shiroFilterFactoryBean.getFilterChainDefinitionMap().clear();
    			shiroFilterFactoryBean.setFilterChainDefinitionMap(this.updateGetFilterChain());
    			shiroFilterFactoryBean.getFilterChainDefinitionMap().forEach((k,v) -> filterChainManager.createChain(k,v));
    		} catch (Exception e) {
    			LOGGER.error(e.getMessage(),e);
    		}
    	}
		
//		System.out.println("修改/删除api角色，rule.getNeedRoles() == " +  rule.getNeedRoles());
		if("".equals(rule.getNeedRoles()) || rule.getNeedRoles() == null) {
			//当资源对应的角色
			apiAuthCacheService.remove(RedisConstance.API_RESOURCE_ROLE+rule.getUrl());
		}else {
			apiAuthCacheService.put(RedisConstance.API_RESOURCE_ROLE+rule.getUrl(), rule.getNeedRoles());
		}
    	
    	//更新缓存
    }

	/**
	 * 将所有API资源保存到缓存
	 */
	public void initAllApiRoute() {
    	List<AuthResource> allApi = resourceService.getApiList();
    	routeCacheService.initApiRouteInfo(allApi);
    }
}
