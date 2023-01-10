package com.rany.cake.dingtalk.starter;

import com.rany.cake.dingtalk.sdk.configuration.SsoConstants;
import org.springframework.context.annotation.Condition;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.core.env.Environment;
import org.springframework.core.type.AnnotatedTypeMetadata;

/**
 * @author tutu
 */
public class WebAppCondition implements Condition {

    @Override
    public boolean matches(ConditionContext context, AnnotatedTypeMetadata metadata) {
        // 获取当前环境信息
        Environment environment = context.getEnvironment();
        String property = environment.getProperty("easy.sso.client.client-type", SsoConstants.WEB_CLIENT);
        if (property.equals(SsoConstants.WEB_CLIENT)) {
            return true;
        }
        return false;

    }
}
