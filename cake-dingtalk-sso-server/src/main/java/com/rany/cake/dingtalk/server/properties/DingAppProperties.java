package com.rany.cake.dingtalk.server.properties;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * @author tutu
 */
@Setter
@Getter
@Configuration
@ConfigurationProperties(prefix = "dingtalk")
public class DingAppProperties {
    private String appKey;
    private String appSecret;
    private String agentId;
    private String corpId;
}
