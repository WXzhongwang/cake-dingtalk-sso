package com.rany.cake.dingtalk.server;

import org.springframework.stereotype.Component;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebFilter(urlPatterns = {"/*"})
@Component
public class FirstFilter implements Filter {

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse res = (HttpServletResponse) response;

        res.addHeader("Content-Security-Policy",
                "script-src 'unsafe-inline' 'unsafe-eval' 'self' *.alipay.com *.aliyun.com *.dingtalk.com *.alicdn.com https://ynuf.alipay.com/uid https://cfd.aliyun.com/collector/analyze.jsonp");
        chain.doFilter(req, res);
    }
}