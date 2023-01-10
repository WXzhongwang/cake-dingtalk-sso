package com.rany.cake.dingtalk.starter.annotation.validator;

import com.rany.cake.dingtalk.starter.annotation.StrValueRange;
import org.springframework.util.ObjectUtils;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.Objects;

public class StrValueRangeValidator implements ConstraintValidator<StrValueRange, Object> {

    private String[] values;

    @Override
    public void initialize(StrValueRange strValueRange) {
        this.values = strValueRange.values();
    }

    @Override
    public boolean isValid(Object o, ConstraintValidatorContext constraintValidatorContext) {

        if (ObjectUtils.isEmpty(o)) {
            return true;
        }

        for (String value : values) {
            if (Objects.equals(o, value)) {
                return true;
            }
        }

        return false;
    }
}
