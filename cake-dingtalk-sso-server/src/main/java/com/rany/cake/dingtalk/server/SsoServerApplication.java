package com.rany.cake.dingtalk.server;

import com.alibaba.dubbo.config.spring.context.annotation.EnableDubbo;
import com.rany.cake.dingtalk.starter.EnableCakeSso;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;


/**
 * @author tutu
 */
@EnableCakeSso
@EnableDubbo
@SpringBootApplication
public class SsoServerApplication {

    public static void main(String[] args) {
        SpringApplication.run(SsoServerApplication.class);
    }
}
