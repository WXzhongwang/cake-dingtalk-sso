package com.rany.cake.dingtalk.starter;

import com.rany.cake.dingtalk.sdk.properties.JedisConfig;
import com.rany.cake.dingtalk.sdk.utils.JedisUtil;
import org.springframework.beans.BeanUtils;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author tutu
 */
@Configuration
@ConditionalOnProperty(prefix = "easy.sso.redis", value = "address")
@AutoConfigureAfter(SsoAutoConfiguration.class)
public class JedisAutoConfiguration {

    @Bean
    public JedisProperties jedisProperties() {
        return new JedisProperties();
    }

    @Bean
    public void initJedis(){
        // 初始化jedis
        JedisConfig jedisConfig = new JedisConfig();
        BeanUtils.copyProperties(jedisProperties(), jedisConfig);
        JedisUtil.init(jedisConfig);
    }
}
