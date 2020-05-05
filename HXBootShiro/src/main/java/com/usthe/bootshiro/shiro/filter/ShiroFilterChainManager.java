package com.usthe.bootshiro.shiro.filter;


import com.usthe.bootshiro.service.AccountService;
import com.usthe.bootshiro.shiro.config.RestPathMatchingFilterChainResolver;
import com.usthe.bootshiro.shiro.provider.ShiroFilterRulesProvider;
import com.usthe.bootshiro.shiro.rule.RolePermRule;
import com.usthe.bootshiro.support.SpringContextHolder;
import org.apache.shiro.spring.web.ShiroFilterFactoryBean;
import org.apache.shiro.web.filter.mgt.DefaultFilterChainManager;
import org.apache.shiro.web.servlet.AbstractShiroFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;
import javax.servlet.Filter;
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
	
	
    private static final Logger LOGGER = LoggerFactory.getLogger(ShiroFilterChainManager.class);

    private final String API_RESOURCE_ROLE = "API:";
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
     * @return java.util.Map<java.lang.String,javax.servlet.Filter>
     */
    public Map<String,Filter> initGetFilters() {
        Map<String,Filter> filters = new LinkedHashMap<>();
        
        PasswordFilter passwordFilter = new PasswordFilter();
        passwordFilter.setRedisTemplate(redisTemplate);
        filters.put("auth",passwordFilter);
        
        BonJwtFilter jwtFilter = new BonJwtFilter();
        jwtFilter.setRedisTemplate(redisTemplate);
        jwtFilter.setAccountService(accountService);
        filters.put("jwt",jwtFilter);
        return filters;
    }
    /**
     * description 初始化获取过滤链规则--项目启动时会执行
         *  将需要拦截的路径以及对应使用的过滤器统一配置好
     * @return java.util.Map<java.lang.String,java.lang.String>
     */
    public Map<String,String> initGetFilterChain() {
        Map<String,String> filterChain = new LinkedHashMap<>();
        // -------------anon 默认过滤器忽略的URL
        List<String> defalutAnon = Arrays.asList("/css/**","/js/**");
        defalutAnon.forEach(ignored -> filterChain.put(ignored,"anon"));
        
        // -------------auth 默认需要认证过滤器的URL 走auth--PasswordFilter
        //登录和注册都是走这个过滤器
        List<String> defalutAuth = Arrays.asList("/account/**");
        defalutAuth.forEach(auth -> filterChain.put(auth,"auth"));
        
        // -------------dynamic 动态URL
        if (shiroFilterRulesProvider != null) {
            /**
             * 从数据库中获取所有角色和资源（API路径和请求类型 ）的对应关系
             * 
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
                    StringBuilder chain = rule.toFilterChain();
                    if (null != chain) {
                      //实例 chain.toString() = “jwt[role_guest,role_application_uiotcp_portal,role_admin]”jwt为过滤器名称
                        filterChain.putIfAbsent(rule.getUrl(),chain.toString());
                        redisTemplate.opsForValue().set(API_RESOURCE_ROLE+rule.getUrl(), rule.getNeedRoles());
                        System.out.println("存在的过滤器==="+rule.getUrl()+"==="+chain.toString());
                        LOGGER.info("存在的过滤器==="+rule.getUrl()+"==="+chain.toString());
                    }
                });
            }
        }
        return filterChain;
    }
    /**
     * description 重新加载过滤链规则（清空缓存中原过滤器链）
     * 当角色授权的资源动态更改之后 需要重新加载过滤器链规则
     */
    public void reloadFilterChain() {
            ShiroFilterFactoryBean shiroFilterFactoryBean = SpringContextHolder.getBean(ShiroFilterFactoryBean.class);
            AbstractShiroFilter abstractShiroFilter = null;
            try {
                abstractShiroFilter = (AbstractShiroFilter)shiroFilterFactoryBean.getObject();
                RestPathMatchingFilterChainResolver filterChainResolver = (RestPathMatchingFilterChainResolver)abstractShiroFilter.getFilterChainResolver();
                DefaultFilterChainManager filterChainManager = (DefaultFilterChainManager)filterChainResolver.getFilterChainManager();
              //清空原过滤器链缓存
                filterChainManager.getFilterChains().clear();
                shiroFilterFactoryBean.getFilterChainDefinitionMap().clear();
                shiroFilterFactoryBean.setFilterChainDefinitionMap(this.initGetFilterChain());
                shiroFilterFactoryBean.getFilterChainDefinitionMap().forEach((k,v) -> filterChainManager.createChain(k,v));
            } catch (Exception e) {
                LOGGER.error(e.getMessage(),e);
            }
    }
}
