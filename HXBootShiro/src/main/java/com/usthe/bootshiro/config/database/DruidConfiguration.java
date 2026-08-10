package com.usthe.bootshiro.config.database;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Configuration;

/**
 *  durid 监控页面配置 默认地址 localhost:8080/druid/login.html
 *  注意：druid 1.2.28 的 StatViewServlet/WebStatFilter 仍基于 javax.servlet 编译，
 *  Spring Boot 3 (jakarta.servlet) 下无法直接注册，监控页功能暂禁用。
 *  数据源本身（DruidDataSource）不依赖 servlet，不受影响。
 *  @author tomsun28
 *  @date 15:40 2018/3/5
 */
@Configuration
public class DruidConfiguration {

    private static final Logger LOGGER = LoggerFactory.getLogger(DruidConfiguration.class);

    // ===== Spring Boot 3 兼容处理：druid 监控页 (StatViewServlet/WebStatFilter) 基于 javax.servlet，
    // ===== Boot 3 容器是 jakarta.servlet，直接注册会报 "cannot access javax.servlet.http.HttpServlet"。
    // ===== 如需恢复监控页：升级到支持 jakarta 的 druid 版本，或改用 druid-spring-boot-3-starter 的自动配置。
    // ===== 数据源 DruidDataSource 的配置见 DataSourceConfiguration，功能不受影响。

//    /**
//         * 配置druid的监控程序参数
//         * 监控页面路径：http://localhost:8080/druid/index.html
//     * @return
//     */
//    @Bean
//    public ServletRegistrationBean druidServlet() {
//        LOGGER.info("init Druid Servlet Configuration ");
//        ServletRegistrationBean servletRegistrationBean = new ServletRegistrationBean(new StatViewServlet(), "/druid/*");
//        // IP白名单
//        //("allow", "192.168.2.25,116.196.81.106,127.0.0.1");
//        // IP黑名单(共同存在时，deny优先于allow)
//        //("deny", "");
//        //控制台管理用户
//        servletRegistrationBean.addInitParameter("loginUsername", "admin");
//        servletRegistrationBean.addInitParameter("loginPassword", "admin");
//        //是否能够重置数据 禁用HTML页面上的"Reset All"功能
//        servletRegistrationBean.addInitParameter("resetEnable", "false");
//        return servletRegistrationBean;
//    }
//    @Bean
//    @SuppressWarnings("unchecked")
//    public FilterRegistrationBean filterRegistrationBean() {
//        /**
//                 *将自定义过滤器加入到过滤器链中
//         */
//        FilterRegistrationBean filterRegistrationBean = new FilterRegistrationBean(new WebStatFilter());
//      //过滤应用程序中所有资源,当前应用程序根下的所有文件包括多级子目录下的所有文件，注意这里*前有"/"
//        filterRegistrationBean.addUrlPatterns("/*");
//        filterRegistrationBean.addInitParameter("exclusions", "*.js,*.gif,*.jpg,*.png,*.css,*.ico,/druid/*");
//        return filterRegistrationBean;
//    }

}
