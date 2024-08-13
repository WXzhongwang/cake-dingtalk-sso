package com.rany.cake.dingtalk.starter;


import com.rany.cake.dingtalk.sdk.configuration.SsoConstants;
import com.rany.cake.dingtalk.sdk.properties.AjaxFailureResponse;
import com.rany.cake.dingtalk.starter.annotation.StrValueRange;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.NotEmpty;

/**
 * @author tutu
 */
@Setter
@Getter
@Validated
@ConfigurationProperties(prefix = "easy.sso.client")
public class SsoProperties {

    private boolean enable;

    @StrValueRange(values = {SsoConstants.WEB_CLIENT, SsoConstants.APP_CLIENT}, message = "clientType只能为web或app")
    private String clientType = SsoConstants.WEB_CLIENT;

    private String[] ignoreUrls = {};

    private String[] ignoreResources = {".js", ".css", ".jpg", ".png", ".ico", ".html"};

    @NotEmpty
    private String ssoServer;
    @NotEmpty
    private String ssoCallbackUrl;

    @StrValueRange(values = {SsoConstants.WEB_CLIENT_LOGOUT_URL, SsoConstants.APP_CLIENT_LOGOUT_URL})
    private String logoutUrl = SsoConstants.WEB_CLIENT_LOGOUT_URL;

    @NotEmpty
    private String loginUrl;

    private AjaxFailureResponse ajaxFailureResponse;

}
