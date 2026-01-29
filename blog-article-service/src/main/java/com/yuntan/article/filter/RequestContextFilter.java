package com.yuntan.article.filter;

import com.yuntan.common.context.BaseContext;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Component;

import java.io.IOException;

// 并创建一个过滤器来设置请求上下文
@Component
public class RequestContextFilter implements Filter {
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        // 设置请求上下文
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        // 将请求对象设置为请求上下文
        BaseContext.setRequest(httpRequest);
        try {
            chain.doFilter(request, response);
        } finally {
            BaseContext.remove();
        }
    }
}