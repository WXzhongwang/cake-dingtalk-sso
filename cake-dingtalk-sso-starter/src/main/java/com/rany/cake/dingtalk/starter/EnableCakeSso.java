package com.rany.cake.dingtalk.starter;


import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

/**
 * @author tutu
 */
@Import({SsoAutoConfiguration.class, JedisAutoConfiguration.class})
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
public @interface EnableCakeSso {
}
