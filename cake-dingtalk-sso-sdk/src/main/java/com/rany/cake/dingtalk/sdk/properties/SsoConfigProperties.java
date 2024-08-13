package com.rany.cake.dingtalk.sdk.properties;

import com.rany.cake.dingtalk.sdk.configuration.SsoConstants;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.springframework.util.ObjectUtils;


/**
 * @author tutu
 */
@Slf4j
@Setter
@Getter
public class SsoConfigProperties {

    private static SsoConfigProperties ssoProperties;

    /**
     * 应用类型(默认web服务)
     */
    private String clientType = SsoConstants.WEB_CLIENT;

    /**
     * 放行的url
     */
    private String[] ignoreUrls = {};

    /**
     * 放行的资源文件
     */
    private String[] ignoreResources = {".js", ".css", ".jpg", ".png", ".ico", ".html"};

    /**
     * sso服务器地址
     */
    private String ssoServer;
    private String ssoCallbackUrl;

    private String logoutUrl = SsoConstants.WEB_CLIENT_LOGOUT_URL;

    private String loginUrl;

    private AjaxFailureResponse ajaxFailureResponse;

    private JedisConfig jedisConfig;

    private static final String CLIENT_TYPE = "clientType";

    private static final String IGNORE_URLS = "ignoreUrls";

    private static final String IGNORE_RESOURCES = "ignoreResources";

    private static final String SSO_SERVER = "ssoServer";

    private static final String SSO_CALLBACK_URL = "ssoCallbackUrl";

    private static final String LOGOUT_URL = "logoutUrl";

    private static final String LOGIN_URL = "loginUrl";

    private static final String AJAX_FAILURE_RESPONSE = "ajaxFailureResponse";

    private static final String CONTENT_TYPE = "contentType";

    private static final String CODE = "code";

    private static final String MSG = "msg";

    private static final String JEDIS_CONFIG = "jedisConfig";

    private static final String ADDRESS = "address";

    private static final String PASSWORD = "password";

    private static final String MAT_TOTAL = "maxTotal";

    private static final String MAX_IDLE = "maxIdle";

    private static final String MIN_IDLE = "minIdle";

    private static final String MAX_WAIT_MILLIS = "maxWaitMillis";

    private static final String SEPARATOR = " ";


    public synchronized static SsoConfigProperties getProperties(String fileName) throws DocumentException, IllegalAccessException, InstantiationException, ClassNotFoundException {
        if (ssoProperties != null) {
            return ssoProperties;
        }

        SAXReader saxReader = new SAXReader();
        Document doc = saxReader.read(SsoConfigProperties.class.getClassLoader().getResourceAsStream(fileName));
        //获取根元素
        Element root = doc.getRootElement();
        SsoConfigProperties sp = new SsoConfigProperties();
        // 获取客户端类型
        sp.clientType = root.element(CLIENT_TYPE).getTextTrim();
        // 获取不需要验证的路径
        String ignoreUrls = root.element(IGNORE_URLS).getTextTrim();
        if (StringUtils.isNotEmpty(ignoreUrls)) {
            sp.ignoreUrls = ignoreUrls.split(SEPARATOR);
        }
        // 获取不需要验证的静态资源
        String ignoreResources = root.element(IGNORE_RESOURCES).getTextTrim();
        if (StringUtils.isNotEmpty(ignoreResources)) {
            sp.ignoreResources = ignoreResources.split(SEPARATOR);
        }
        // 获取SSO Server服务地址
        sp.ssoServer = root.element(SSO_SERVER).getTextTrim();
        sp.ssoCallbackUrl = root.element(SSO_CALLBACK_URL).getTextTrim();
        // 获取退出接口
        sp.logoutUrl = root.element(LOGOUT_URL).getTextTrim();
        // 获取登录地址
        sp.loginUrl = root.element(LOGIN_URL).getTextTrim();
        Element ajaxFailureResponse = root.element(AJAX_FAILURE_RESPONSE);
        if (!ObjectUtils.isEmpty(ajaxFailureResponse)) {
            AjaxFailureResponse failureResponse = new AjaxFailureResponse();
            failureResponse.setContentType(ajaxFailureResponse.element(CONTENT_TYPE).getTextTrim());
            failureResponse.setCode(Integer.valueOf(ajaxFailureResponse.element(CODE).getTextTrim()));
            failureResponse.setMsg(ajaxFailureResponse.element(MSG).getTextTrim());
            sp.ajaxFailureResponse = failureResponse;
        }

        Element jedisConfig = root.element(JEDIS_CONFIG);
        if (!ObjectUtils.isEmpty(jedisConfig)) {
            JedisConfig config = new JedisConfig();
            config.setAddress(jedisConfig.element(ADDRESS).getTextTrim());
            if (!ObjectUtils.isEmpty(jedisConfig.element(PASSWORD))) {
                config.setPassword(jedisConfig.element(PASSWORD).getTextTrim());
            }
            config.setMaxTotal(Integer.parseInt(jedisConfig.element(MAT_TOTAL).getTextTrim()));
            config.setMaxIdle(Integer.parseInt(jedisConfig.element(MAX_IDLE).getTextTrim()));
            config.setMinIdle(Integer.parseInt(jedisConfig.element(MIN_IDLE).getTextTrim()));
            config.setMaxWaitMillis(Integer.parseInt(jedisConfig.element(MAX_WAIT_MILLIS).getTextTrim()));
            sp.jedisConfig = config;
        }
        ssoProperties = sp;
        return ssoProperties;
    }

}
