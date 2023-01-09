package com.rany.cake.dingtalk.sdk.filter;


import com.rany.cake.dingtalk.sdk.configuration.SsoConstants;
import com.rany.cake.dingtalk.sdk.properties.SsoConfigProperties;
import com.rany.cake.dingtalk.sdk.utils.SsoUtil;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @author tutu
 */
@Slf4j
@Setter
@Getter
@NoArgsConstructor
public class SsoWebFilter implements Filter {

    private SsoConfigProperties ssoProperties;

    public SsoWebFilter (SsoConfigProperties ssoProperties){
        this.ssoProperties = ssoProperties;
    }

    @Override
    public void init(FilterConfig config) {
        log.info("SsoWebFilter init...");
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest)request;
        HttpServletResponse res = (HttpServletResponse)response;

        // 设置跨域
        SsoUtil.setRequestCross(req, res);

        //访问路径
        String url = req.getRequestURI();

        // 不需要验证,直接放行
        if (!url.equals(SsoConstants.WEB_CLIENT_LOGOUT_URL) && SsoUtil.isAllow(url, ssoProperties)) {
            chain.doFilter(request, response);
            return;
        }

        // 退出
        if (ssoProperties.getLogoutUrl().equals(url)) {
            SsoUtil.invalidateSession(req);
            SsoUtil.removeCookie(req, res, SsoConstants.SSO_SESSION_ID);
            SsoUtil.redirectLogin(req, res, ssoProperties);
            return;
        }

        // 判断是否已经登录
        if (SsoUtil.isLogined(req, res)) {
            chain.doFilter(request, response);
            return;
        }

        if (SsoUtil.isAjaxRequest(req)) {
            SsoUtil.ajaxAuthFailure(res, ssoProperties);
        } else {
            SsoUtil.redirectLogin(req, res, ssoProperties);
        }
    }



    @Override
    public void destroy() {
        log.info("SsoWebFilter destroy...");
    }
}
