package com.sotory.paymentDiary.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Documented
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface NotBlankIfPresent {
    String message() default "공백은 허용되지 않습니다";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
