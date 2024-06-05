package com.rany.cake.dingtalk.sdk.configuration;

/**
 * @author tutu
 */
public final class SsoConstants {

    /**
     * 存在Cookie中用户信息的键
     */
    public static final String SSO_SESSION_ID = "RANY_CAKE_SSO";

    /**
     * 存在session中用户信息的键
     */
    public static final String CURRENT_USER = "currentUser";

    /**
     * 参数地址
     */
    public static final String WEBAPP = "webapp";

    /**
     * web客户端
     */
    public static final String WEB_CLIENT = "web";

    /**
     * app客户端
     */
    public static final String APP_CLIENT = "app";

    /**
     * 未知的用户信息
     */
    public static final String AUTH_FAILED_WARN = "未知的用户信息";
    public static final String ACCOUNT_REGISTER_FAILED = "企业应用新用户注册失败";

    /**
     * app客户端退出接口地址
     */
    public static final String APP_CLIENT_LOGOUT_URL = "/oauth/logout";

    /**
     * 存储用户前缀
     */
    public static final String STORAGE_USER_PREFIX = "CAKE_DINGTALK_SSO_";

    /**
     * web客户端退出接口地址
     */
    public static final String WEB_CLIENT_LOGOUT_URL = "/sso/logout";

    /**
     * token认证请求头
     */
    public static final String TOKEN_AUTH_HEADER = "accessToken";

    /**
     * refreshToken前缀
     */
    public static final String REFRESH_TOKEN_PREFIX = "refreshToken_";

    /**
     * refreshToken有效期默认15天
     */
    public static final Integer REFRESH_TOKEN_MAX_AGE = 15 * 24 * 60 * 60;

}
