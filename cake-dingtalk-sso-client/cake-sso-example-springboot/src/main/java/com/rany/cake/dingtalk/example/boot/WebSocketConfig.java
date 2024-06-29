package com.rany.cake.dingtalk.example.boot;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;
import org.springframework.web.socket.server.standard.ServletServerContainerFactoryBean;

@Configuration
@EnableWebSocket
public class WebSocketConfig implements WebSocketConfigurer {


    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(new TestMessageHandler(), "/api/ws")
                .addInterceptors(new TestMessageInterceptor())
                .setAllowedOrigins("*");
    }

    /**
     * web socket 缓冲区大小配置
     */
    @Bean
    public ServletServerContainerFactoryBean servletServerContainerFactoryBean() {
        ServletServerContainerFactoryBean factory = new ServletServerContainerFactoryBean();
        factory.setMaxBinaryMessageBufferSize(1024 * 1024);
        factory.setMaxTextMessageBufferSize(1024 * 1024);
        factory.setMaxSessionIdleTimeout(30 * 60000L);
        return factory;
    }
}
