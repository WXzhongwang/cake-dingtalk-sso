package com.rany.cake.dingtalk.starter;

import com.rany.cake.dingtalk.sdk.filter.SsoTokenFilter;
import com.rany.cake.dingtalk.sdk.filter.SsoWebFilter;
import com.rany.cake.dingtalk.sdk.properties.SsoConfigProperties;
import com.rany.cake.dingtalk.starter.resolver.LoginUserMethodArgumentResolver;
import org.springframework.beans.BeanUtils;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;

/**
 * @author tutu
 */
@Configuration
@ConditionalOnProperty(prefix = "easy.sso.client", value = "enable", matchIfMissing = true)
public class SsoAutoConfiguration implements WebMvcConfigurer {

    @Bean
    public SsoProperties ssoProperties() {
        return new SsoProperties();
    }

    @Bean
    @Conditional(WebAppCondition.class)
    public FilterRegistrationBean ssoWebFilter() {
        SsoConfigProperties properties = new SsoConfigProperties();
        BeanUtils.copyProperties(ssoProperties(), properties);

        FilterRegistrationBean bean = new FilterRegistrationBean();
        bean.setOrder(1);
        bean.setFilter(new SsoWebFilter(properties));
        bean.addUrlPatterns("/*");
        return bean;
    }

    @Bean
    @Conditional(AppCondition.class)
    public FilterRegistrationBean ssoTokenFilter() {
        SsoConfigProperties properties = new SsoConfigProperties();
        BeanUtils.copyProperties(ssoProperties(), properties);

        FilterRegistrationBean bean = new FilterRegistrationBean();
        bean.setOrder(1);
        bean.setFilter(new SsoTokenFilter(properties));
        bean.addUrlPatterns("/*");
        return bean;
    }


    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
        resolvers.add(new LoginUserMethodArgumentResolver(ssoProperties()));
    }
}
