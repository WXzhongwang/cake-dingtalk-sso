package com.rany.cake.dingtalk.starter;

import com.rany.cake.dingtalk.sdk.filter.SsoTokenFilter;
import com.rany.cake.dingtalk.sdk.filter.SsoWebFilter;
import com.rany.cake.dingtalk.sdk.properties.SsoConfigProperties;
import org.springframework.beans.BeanUtils;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Configuration;


/**
 * @author tutu
 */
@Configuration
@ConditionalOnProperty(prefix = "easy.sso.client", value = "enable", matchIfMissing = true)
public class SsoAutoConfiguration {

    @Bean
    public SsoProperties ssoProperties() {
        return new SsoProperties();
    }

    @Bean
    @Conditional(WebAppCondition.class)
    public FilterRegistrationBean<SsoWebFilter> ssoWebFilter() {
        SsoConfigProperties properties = new SsoConfigProperties();
        BeanUtils.copyProperties(ssoProperties(), properties);

        FilterRegistrationBean<SsoWebFilter> bean = new FilterRegistrationBean<>();
        bean.setOrder(1);
        bean.setFilter(new SsoWebFilter(properties));
        bean.addUrlPatterns("/*");
        return bean;
    }

    @Bean
    @Conditional(AppCondition.class)
    public FilterRegistrationBean<SsoTokenFilter> ssoTokenFilter() {
        SsoConfigProperties properties = new SsoConfigProperties();
        BeanUtils.copyProperties(ssoProperties(), properties);

        FilterRegistrationBean<SsoTokenFilter> bean = new FilterRegistrationBean<>();
        bean.setOrder(1);
        bean.setFilter(new SsoTokenFilter(properties));
        bean.addUrlPatterns("/*");
        return bean;
    }
}
