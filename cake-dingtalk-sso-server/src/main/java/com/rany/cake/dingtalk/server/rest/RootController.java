package com.rany.cake.dingtalk.server.rest;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author zhongshengwang
 * @description TODO
 * @date 2021/11/3 10:59 下午
 * @email 18668485565@163.com
 */
@RestController
public class RootController {

    @GetMapping("/ok")
    public String ok() {
        return "ok";
    }
}
