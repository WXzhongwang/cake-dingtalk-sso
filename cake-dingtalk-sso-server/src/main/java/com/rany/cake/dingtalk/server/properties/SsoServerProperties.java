package com.rany.cake.dingtalk.server.properties;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author tutu
 */
@Setter
@Getter
@ConfigurationProperties(prefix = "easy.sso.server")
public class SsoServerProperties {

    /**
     * 服务器登录有效时间
     */
    private Integer maxAge = 1800;

    /**
     * 签发者
     */
    private String issuser = "easy-sso-server";

    /**
     * 密钥
     */
    private String secretKey = "!@#$%^&*";

}
