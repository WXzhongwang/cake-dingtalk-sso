package com.rany.cake.dingtalk.example.boot;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import javax.servlet.http.HttpServletRequest;

/**
 * TODO
 *
 * @author zhongshengwang
 * @description TODO
 * @date 2023/8/18 21:55
 * @email 18668485565163.com
 */
@Controller
public class TestController {
    @GetMapping("/")
    public String index(HttpServletRequest request) {
        return "index";
    }
}
