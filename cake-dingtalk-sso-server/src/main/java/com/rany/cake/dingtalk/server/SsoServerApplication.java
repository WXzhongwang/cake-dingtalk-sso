package com.rany.cake.dingtalk.server;

import com.rany.cake.dingtalk.starter.EnableCakeSso;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;


/**
 * @author tutu
 */
@EnableCakeSso
@SpringBootApplication
public class SsoServerApplication {

    public static void main(String[] args) {
        SpringApplication.run(SsoServerApplication.class);
    }
}
