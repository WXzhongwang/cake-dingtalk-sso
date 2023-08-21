package com.rany.cake.dingtalk.example.boot;

import com.rany.cake.dingtalk.starter.EnableCakeSso;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

/**
 * TODO
 *
 * @author zhongshengwang
 * @description TODO
 * @date ${DATE} ${TIME}
 * @email 18668485565163.com
 */
@EnableCakeSso
@ComponentScan(basePackages = "org.example")
@SpringBootApplication
public class Main {
    public static void main(String[] args) {
        SpringApplication.run(Main.class, args);
    }
}