package com.usthe.bootshiro.shiro.config;

import com.usthe.bootshiro.shiro.filter.StatelessWebSubjectFactory;
import com.usthe.bootshiro.shiro.filter.ShiroFilterChainManager;
import com.usthe.bootshiro.shiro.realm.AonModularRealmAuthenticator;
import com.usthe.bootshiro.shiro.realm.RealmManager;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.mgt.DefaultSessionStorageEvaluator;
import org.apache.shiro.mgt.DefaultSubjectDAO;
import org.apache.shiro.mgt.SecurityManager;
import org.apache.shiro.spring.web.ShiroFilterFactoryBean;
import org.apache.shiro.web.mgt.DefaultWebSecurityManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 *   shiro配置
 * @author tomsun28
 * @date 12:41 2018/3/6
 */
@Configuration
public class ShiroConfiguration {

    /**
     * 配置ShiroFilterFactoryBean
     * @param securityManager
     * @param filterChainManager
     * @return
     */
    @Bean
    public ShiroFilterFactoryBean shiroFilterFactoryBean(SecurityManager securityManager,ShiroFilterChainManager filterChainManager) {
        RestShiroFilterFactoryBean shiroFilterFactoryBean = new RestShiroFilterFactoryBean();
        shiroFilterFactoryBean.setSecurityManager(securityManager);
        shiroFilterFactoryBean.setFilters(filterChainManager.initGetFilters());//添加所有自定义过滤器
        /**
         * 添加所有自定义的filterChain
         * 这里配置的是url与对应授权角色的关系
         */
        shiroFilterFactoryBean.setFilterChainDefinitionMap(filterChainManager.initGetFilterChain());
        return shiroFilterFactoryBean;
    }

    /**
     * SecurityManager管理着认证、授权、会话管理
     * @param realmManager
     * @return
     */
    @Bean
    public SecurityManager securityManager(RealmManager realmManager) {
        DefaultWebSecurityManager securityManager = new DefaultWebSecurityManager();
        securityManager.setAuthenticator(new AonModularRealmAuthenticator());
        securityManager.setRealms(realmManager.initGetRealm());

        // 无状态subjectFactory设置--不生成session
        DefaultSessionStorageEvaluator evaluator = (DefaultSessionStorageEvaluator)((DefaultSubjectDAO) securityManager.getSubjectDAO()).getSessionStorageEvaluator();
        evaluator.setSessionStorageEnabled(Boolean.FALSE);
        StatelessWebSubjectFactory subjectFactory = new StatelessWebSubjectFactory();
        securityManager.setSubjectFactory(subjectFactory);

        SecurityUtils.setSecurityManager(securityManager);
        return securityManager;
    }

}
