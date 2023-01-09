package com.rany.cake.dingtalk.sdk.filter;

import com.alibaba.fastjson.JSON;
import com.rany.cake.dingtalk.sdk.configuration.SsoConstants;
import com.rany.cake.dingtalk.sdk.properties.SsoConfigProperties;
import com.rany.cake.dingtalk.sdk.result.ReturnT;
import com.rany.cake.dingtalk.sdk.utils.SsoTokenLoginHelper;
import com.rany.cake.dingtalk.sdk.utils.SsoUtil;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.PathMatcher;

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
public class SsoTokenFilter implements Filter {

    private SsoConfigProperties ssoProperties;

    private static final PathMatcher PATH_MATCHER = new AntPathMatcher();

    public SsoTokenFilter (SsoConfigProperties ssoProperties){
        this.ssoProperties = ssoProperties;
    }

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        log.info("SsoTokenFilter init...");
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
        if (!url.equals(SsoConstants.APP_CLIENT_LOGOUT_URL) && SsoUtil.isAllow(url, ssoProperties)) {
            chain.doFilter(request, response);
            return;
        }


        String token = req.getHeader(SsoConstants.TOKEN_AUTH_HEADER);

        //  请求头中未携带token
        if (StringUtils.isEmpty(token) && !url.equals(SsoConstants.APP_CLIENT_LOGOUT_URL)) {
            // 验证失败
            SsoUtil.ajaxAuthFailure(res, ssoProperties);
            return;
        }

        // 退出登录
        if (ssoProperties.getLogoutUrl().equals(url)) {
            // 清除缓存中用户信息
            SsoTokenLoginHelper.removeStorageUser(token);
            // 返回响应信息
            res.setStatus(HttpServletResponse.SC_OK);
            res.setContentType("application/json;charset=UTF-8");
            res.getWriter().println(JSON.toJSONString(ReturnT.success()));
            return;
        }


        // 判断是否已经登录
        if (SsoTokenLoginHelper.isLogined(req, token)) {
            chain.doFilter(request, response);
            return;
        }

        // 返回认证失败信息
        SsoUtil.ajaxAuthFailure(res, ssoProperties);
    }

    @Override
    public void destroy() {
        log.info("SsoTokenFilter destroy...");
    }
}
