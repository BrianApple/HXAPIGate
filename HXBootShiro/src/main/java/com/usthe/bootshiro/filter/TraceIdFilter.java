package com.usthe.bootshiro.filter;

import java.io.IOException;
import java.util.UUID;

import org.slf4j.MDC;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * 管理端请求溯源 Filter：
 * <ul>
 *   <li>为每个请求生成/复用 traceId 写入 slf4j MDC（logback pattern 中 %X{traceId} 输出到每行日志）</li>
 *   <li>通过 X-Trace-Id 响应头回传调用方（网关转发请求时同样携带，便于跨服务链路联查）</li>
 *   <li>协议固定标识为 http（管理端仅 HTTP 接口）</li>
 * </ul>
 */
@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class TraceIdFilter extends OncePerRequestFilter {

    public static final String TRACE_ID = "traceId";
    public static final String PROTO = "proto";
    public static final String HEADER_X_TRACE_ID = "X-Trace-Id";

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {
        String traceId = request.getHeader(HEADER_X_TRACE_ID);
        if (traceId == null || traceId.trim().isEmpty()) {
            traceId = UUID.randomUUID().toString().replace("-", "").substring(0, 16);
        } else {
            traceId = traceId.trim();
        }
        MDC.put(TRACE_ID, traceId);
        MDC.put(PROTO, "http");
        response.setHeader(HEADER_X_TRACE_ID, traceId);
        try {
            chain.doFilter(request, response);
        } finally {
            MDC.remove(TRACE_ID);
            MDC.remove(PROTO);
        }
    }
}
