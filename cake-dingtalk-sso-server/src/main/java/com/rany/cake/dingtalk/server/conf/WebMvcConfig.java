package com.rany.cake.dingtalk.server.conf;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * @author tutu
 */
@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
        registry.addViewController("/").setViewName("login");
        registry.addViewController("/sso/logout").setViewName("login");
        registry.addViewController("/logout").setViewName("login");
        registry.addViewController("/admin").setViewName("admin");
        registry.addViewController("/useCourse").setViewName("useCourse");
    }
}
