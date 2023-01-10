package com.rany.cake.dingtalk.server.conf;

import com.rany.cake.dingtalk.server.properties.SsoServerProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * @author tutu
 */
@Configuration
@EnableConfigurationProperties(SsoServerProperties.class)
public class SsoServerConfig {


}
