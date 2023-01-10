package com.rany.cake.dingtalk.starter.annotation;

import com.rany.cake.dingtalk.starter.annotation.validator.StrValueRangeValidator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

/**
 * @author tutu
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD, ElementType.ANNOTATION_TYPE})
@Constraint(validatedBy = StrValueRangeValidator.class)
public @interface StrValueRange {

    String[] values();

    String message() default "不在取值范围之内";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
