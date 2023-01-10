package com.rany.cake.dingtalk.starter.resolver;


import com.rany.cake.dingtalk.sdk.beans.SsoUser;
import com.rany.cake.dingtalk.sdk.configuration.SsoConstants;
import com.rany.cake.dingtalk.sdk.utils.SsoTokenLoginHelper;
import com.rany.cake.dingtalk.sdk.utils.SsoUtil;
import com.rany.cake.dingtalk.starter.SsoProperties;
import com.rany.cake.dingtalk.starter.annotation.CurrentUser;
import org.springframework.core.MethodParameter;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

import javax.servlet.http.HttpServletRequest;

/**
 * @author tutu
 */
public class LoginUserMethodArgumentResolver implements HandlerMethodArgumentResolver {

    private SsoProperties ssoProperties;

    public LoginUserMethodArgumentResolver(SsoProperties ssoProperties) {
        this.ssoProperties = ssoProperties;
    }

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return parameter.hasParameterAnnotation(CurrentUser.class) && parameter.getParameterType().equals(SsoUser.class);
    }

    @Override
    public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer,
                                  NativeWebRequest webRequest, WebDataBinderFactory binderFactory) throws Exception {
        HttpServletRequest request = (HttpServletRequest) webRequest.getNativeRequest();

        if (ssoProperties.getClientType().equals(SsoConstants.WEB_CLIENT)) {
            return SsoUtil.getCurrentUser(request);
        } else {
            String token = request.getHeader(SsoConstants.TOKEN_AUTH_HEADER);
            return SsoTokenLoginHelper.getStorageUser(token);
        }
    }
}
