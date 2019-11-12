package com.usthe.bootshiro.config.database;

import com.alibaba.druid.support.http.StatViewServlet;
import com.alibaba.druid.support.http.WebStatFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 *  durid 监控页面配置 默认地址 localhost:8080/druid/login.html
 * @author tomsun28
 * @date 15:40 2018/3/5
 */
@Configuration
public class DruidConfiguration {

    private static final Logger LOGGER = LoggerFactory.getLogger(DruidConfiguration.class);



    /**
         * 配置druid的监控程序参数
         * 监控页面路径：http://localhost:8080/druid/index.html
     * @return
     */
    @Bean
    public ServletRegistrationBean druidServlet() {
        LOGGER.info("init Druid Servlet Configuration ");
        ServletRegistrationBean servletRegistrationBean = new ServletRegistrationBean(new StatViewServlet(), "/druid/*");
        // IP白名单
        //("allow", "192.168.2.25,116.196.81.106,127.0.0.1");
        // IP黑名单(共同存在时，deny优先于allow)
        //("deny", "");
        //控制台管理用户
        servletRegistrationBean.addInitParameter("loginUsername", "admin");
        servletRegistrationBean.addInitParameter("loginPassword", "admin");
        //是否能够重置数据 禁用HTML页面上的“Reset All”功能
        servletRegistrationBean.addInitParameter("resetEnable", "false");
        return servletRegistrationBean;
    }
    @Bean
    @SuppressWarnings("unchecked")
    public FilterRegistrationBean filterRegistrationBean() {
        /**
                 *将自定义过滤器加入到过滤器链中
         */
        FilterRegistrationBean filterRegistrationBean = new FilterRegistrationBean(new WebStatFilter());
      //过滤应用程序中所有资源,当前应用程序根下的所有文件包括多级子目录下的所有文件，注意这里*前有“/”
        filterRegistrationBean.addUrlPatterns("/*");
        filterRegistrationBean.addInitParameter("exclusions", "*.js,*.gif,*.jpg,*.png,*.css,*.ico,/druid/*");
        return filterRegistrationBean;
    }

}
