package com.usthe.bootshiro.shiro.config;


import org.apache.shiro.web.filter.mgt.FilterChainManager;
import org.apache.shiro.web.filter.mgt.NamedFilterList;
import org.apache.shiro.web.filter.mgt.PathMatchingFilterChainResolver;
import org.apache.shiro.web.util.WebUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import java.util.Iterator;
import java.util.Map;

/**
 * 对shiro的改造这里自定义了一些规则:
	shiro过滤器链的url=url+"=="+httpMethod
	eg:对于url="api/resource/",httpMethod="GET"的资源,其拼接出来的过滤器链匹配url=api/resource==GET
	这样对相同的url而不同的访问方式，会判定为不同的资源，即资源不再简单是url，而是url和httpMethod的组合。基于角色的授权模型中，角色所拥有的资源形式为url+"=="+httpMethod。
	这里改变了过滤器的过滤匹配url规则，重写PathMatchingFilterChainResolver的getChain方法，增加对上述规则的url的支持。
 * 
 * @author tomsun28
 * @date 21:12 2018/4/20
 */
public class RestPathMatchingFilterChainResolver extends PathMatchingFilterChainResolver {

    private static final Logger LOGGER = LoggerFactory.getLogger(RestPathMatchingFilterChainResolver.class);
    private static final int NUM_2 = 2;
    private static final String DEFAULT_PATH_SEPARATOR = "/";

    public RestPathMatchingFilterChainResolver() {
        super();
    }

    public RestPathMatchingFilterChainResolver(FilterConfig filterConfig) {
        super(filterConfig);
    }

    /**
     * description TODO 重写filterChain匹配
     *
     * @param request 1
     * @param response 2
     * @param originalChain 3
     * @return javax.servlet.FilterChain
     */
    @Override
    public FilterChain getChain(ServletRequest request, ServletResponse response, FilterChain originalChain) {
        FilterChainManager filterChainManager = this.getFilterChainManager();
        if (!filterChainManager.hasChains()) {
            return null;
        } else {
            String requestURI = this.getPathWithinApplication(request);
            if (requestURI != null && requestURI.endsWith(DEFAULT_PATH_SEPARATOR)) {
                requestURI = requestURI.substring(0, requestURI.length() - 1);
            }
            //获取上下文中包含的所有的有权限控制的资源名称（说白了就是url值）如：/account/login==POST  /resource/api/*/*/*==GET等等
            Iterator var6 = filterChainManager.getChainNames().iterator();

            String pathPattern;
            boolean flag = true;
            String[] strings = null;
            do {
                if (!var6.hasNext()) {
                    return null;
                }

                pathPattern = (String)var6.next();

                strings = pathPattern.split("==");
                if (strings.length == NUM_2) {
                    // 分割出url+httpMethod,判断httpMethod与当前实际的request请求的method是否一致,不一致则直接false
                    if (WebUtils.toHttp(request).getMethod().toUpperCase().equals(strings[1].toUpperCase())) {
                        flag = false;
                    } else {
                        flag = true;
                    }
                } else {
                    flag = false;
                }
                pathPattern = strings[0];
                if (pathPattern != null && pathPattern.endsWith(DEFAULT_PATH_SEPARATOR)) {
                    pathPattern = pathPattern.substring(0, pathPattern.length() -1);
                }
                //遍历获取上下文中与当前请求url和请求方式所对应的过滤器
            } while(!this.pathMatches(pathPattern, requestURI) || flag);

            if (LOGGER.isTraceEnabled()) {
                LOGGER.trace("Matched path pattern [" + pathPattern + "] for requestURI [" + requestURI + "].  Utilizing corresponding filter chain...");
            }
            if (strings.length == NUM_2) {
                pathPattern = pathPattern.concat("==").concat(WebUtils.toHttp(request).getMethod().toUpperCase());
            }
            // FilterChain的名称就是路径匹配符(如：" /** "或 " /bootshiro/* "等)，如果请求URI匹配上了某个FilterChain 
            // 则调用FilterChainManager.proxy方法返回一个FilterChain对象，注意是返回第一个匹配FilterChain
            // 也就是说如果在ini配置文件中配置了多个同名的FilterChain，则只有第一个FilterChain有效
            // 原文链接：https://blog.csdn.net/xtayfjpk/article/details/77923430
            // Map<String, NamedFilterList> filterChains; //key: chain name, value: chain
            return filterChainManager.proxy(originalChain, pathPattern);//实例pathPattern = "/resource/api/*/*/*==GET"
        }
    }

}
