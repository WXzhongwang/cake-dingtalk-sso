package com.rany.cake.dingtalk.starter.annotation;

import java.lang.annotation.*;


/**
 * @author tutu
 */
@Documented
@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
public @interface CurrentUser {

}
