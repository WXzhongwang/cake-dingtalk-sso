package com.rany.cake.dingtalk.sdk.properties;

import lombok.Getter;
import lombok.Setter;

/**
 * @author tutu
 */
@Setter
@Getter
public class JedisConfig {

    private String address;

    private String password;

    private int maxTotal = 200;

    private int maxIdle = 50;

    private int minIdle = 8;

    private int maxWaitMillis = 10000;

}
