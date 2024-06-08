package com.rany.cake.dingtalk.server.conf;

import com.alibaba.dubbo.config.annotation.Reference;
import com.rany.ops.api.facade.account.AccountFacade;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * TODO
 *
 * @author zhongshengwang
 * @description TODO
 * @date 2023/1/10 22:19
 * @email 18668485565163.com
 */
@Configuration
public class DubboConfig {

    @Reference(check = false)
    private AccountFacade accountFacade;

    @Bean
    public AccountFacade accountFacade() {
        return accountFacade;
    }
}
