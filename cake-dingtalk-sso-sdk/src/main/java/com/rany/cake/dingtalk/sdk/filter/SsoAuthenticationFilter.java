package com.rany.cake.dingtalk.sdk.filter;

import com.google.common.base.Preconditions;
import com.rany.cake.dingtalk.sdk.configuration.SsoConstants;
import com.rany.cake.dingtalk.sdk.properties.PropertiesManagerContainer;
import com.rany.cake.dingtalk.sdk.properties.SsoConfigProperties;
import com.rany.cake.dingtalk.sdk.utils.JedisUtil;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.ObjectUtils;

import javax.servlet.*;
import java.io.IOException;

/**
 * @author tutu
 */
@Slf4j
@Setter
@Getter
@NoArgsConstructor
public class SsoAuthenticationFilter implements Filter {

    private SsoConfigProperties ssoProperties;

    private SsoWebFilter ssoWebFilter;

    private SsoTokenFilter ssoTokenFilter;

    @Override
    public void init(FilterConfig config) {
        // 读取xml配置文件初始化参数
        initParameters(config);

        // 初始化的参数校验
        checkParameters(ssoProperties);

        // 根据客户端类型初始化具体Filter
        if (ssoProperties.getClientType().equals(SsoConstants.WEB_CLIENT)) {
            // 初始化web过滤器
            initSsoWebFilter();
        } else {
            // 初始化token过滤器
            initSsoTokenFilter();
        }

        if (!ObjectUtils.isEmpty(ssoProperties.getJedisConfig())
                && StringUtils.isNotEmpty(ssoProperties.getJedisConfig().getAddress())) {
            // 初始化redis
            JedisUtil.init(ssoProperties.getJedisConfig());
        }

    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        if (ssoProperties.getClientType().equals(SsoConstants.WEB_CLIENT)) {
            ssoWebFilter.doFilter(request, response, chain);
        } else {
            ssoTokenFilter.doFilter(request, response, chain);
        }
    }


    @Override
    public void destroy() {
        if (ssoProperties.getClientType().equals(SsoConstants.WEB_CLIENT)) {
            ssoWebFilter.destroy();
        } else {
            ssoTokenFilter.destroy();
        }
        log.info("SsoAuthenticationFilter destroy...");
    }


    private void initParameters(FilterConfig config) {
        log.info("SsoAuthenticationFilter init...");
        if (ObjectUtils.isEmpty(ssoProperties)) {
            log.info("初始化统一认证客户端...");
            String fileName = config.getInitParameter("configFile");
            fileName = StringUtils.isBlank(fileName) ? "cake-sso-client.xml" : fileName;
            log.info("尝试使用配置文件：" + fileName);
            PropertiesManagerContainer.init(fileName);
            try {
                ssoProperties = (SsoConfigProperties) PropertiesManagerContainer.getProperties(SsoConfigProperties.class);
            } catch (Exception e) {
                log.error("参数初始化异常:" + e.getMessage(), e);
            }
        }
    }


    private void checkParameters(SsoConfigProperties ssoProperties) {
        // 校验clientType是否为空
        Preconditions.checkArgument(StringUtils.isNotBlank(ssoProperties.getClientType()), "Parameter %s cannot be empty", "clientType");
        // 校验ssoServer是否为空
        Preconditions.checkArgument(StringUtils.isNotBlank(ssoProperties.getSsoServer()), "Parameter %s cannot be empty", "ssoServer");
        // 校验logoutUrl是否为空
        Preconditions.checkArgument(StringUtils.isNotBlank(ssoProperties.getLogoutUrl()), "Parameter %s cannot be empty", "logoutUrl");
        // 校验loginUrl是否为空
        Preconditions.checkArgument(StringUtils.isNotBlank(ssoProperties.getLoginUrl()), "Parameter %s cannot be empty", "loginUrl");
        // 校验clientType只能为web或者app类型
        Preconditions.checkArgument(ssoProperties.getClientType().equals(SsoConstants.WEB_CLIENT) || ssoProperties.getClientType().equals(SsoConstants.APP_CLIENT),
                "Parameter %s can only be %s or %s", "clientType", SsoConstants.WEB_CLIENT, SsoConstants.APP_CLIENT);
        // 校验logoutUrl只能为/sso/logout或者/oauth/logout
        Preconditions.checkArgument(ssoProperties.getLogoutUrl().equals(SsoConstants.WEB_CLIENT_LOGOUT_URL) || ssoProperties.getLogoutUrl().equals(SsoConstants.APP_CLIENT_LOGOUT_URL),
                "Parameter %s can only be %s or %s", "logoutUrl", SsoConstants.WEB_CLIENT_LOGOUT_URL, SsoConstants.APP_CLIENT_LOGOUT_URL);

    }


    private SsoWebFilter initSsoWebFilter() {
        if (ssoWebFilter == null) {
            ssoWebFilter = new SsoWebFilter(ssoProperties);
        }

        return ssoWebFilter;
    }

    private SsoTokenFilter initSsoTokenFilter() {
        if (ssoTokenFilter == null) {
            ssoTokenFilter = new SsoTokenFilter(ssoProperties);
        }

        return ssoTokenFilter;
    }
}
