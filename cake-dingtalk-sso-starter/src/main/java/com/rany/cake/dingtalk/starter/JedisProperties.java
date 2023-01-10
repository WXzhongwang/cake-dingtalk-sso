package com.rany.cake.dingtalk.starter;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.NotNull;

/**
 * @author tutu
 */
@Setter
@Getter
@Validated
@ConfigurationProperties(prefix = "easy.sso.redis")
public class JedisProperties {

    @NotNull
    private String address;

    private String password;

    private int maxTotal = 200;

    private int maxIdle = 50;

    private int minIdle = 8;

    private int maxWaitMillis = 10000;
}
