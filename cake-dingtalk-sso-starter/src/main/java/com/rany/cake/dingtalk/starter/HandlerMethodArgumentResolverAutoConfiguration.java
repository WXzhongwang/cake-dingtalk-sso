package com.rany.cake.dingtalk.starter;

import com.rany.cake.dingtalk.starter.resolver.LoginUserMethodArgumentResolver;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerAdapter;
import org.springframework.web.servlet.mvc.method.annotation.RequestResponseBodyMethodProcessor;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

/**
 * Reference: <a href="https://www.cnblogs.com/linyb-geek/p/16581892.html">
 * 记一次springboot项目自定义HandlerMethodArgumentResolver不生效原因与解法</>
 *
 * @author zhongshengwang
 */
@Configuration
@AutoConfigureAfter(SsoAutoConfiguration.class)
public class HandlerMethodArgumentResolverAutoConfiguration implements InitializingBean {
    @Resource
    private RequestMappingHandlerAdapter requestMappingHandlerAdapter;
    @Resource
    private SsoProperties ssoProperties;

    @Override
    public void afterPropertiesSet() throws Exception {
        List<HandlerMethodArgumentResolver> argumentResolvers = requestMappingHandlerAdapter.getArgumentResolvers();
        List<HandlerMethodArgumentResolver> customArgumentResolvers = new ArrayList<>();

        for (HandlerMethodArgumentResolver argumentResolver : argumentResolvers) {
            if (argumentResolver instanceof RequestResponseBodyMethodProcessor) {
                customArgumentResolvers.add(new LoginUserMethodArgumentResolver(ssoProperties));
            }
            customArgumentResolvers.add(argumentResolver);
        }

        requestMappingHandlerAdapter.setArgumentResolvers(customArgumentResolvers);
    }
}
