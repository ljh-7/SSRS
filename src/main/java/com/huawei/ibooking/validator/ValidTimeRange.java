package com.huawei.ibooking.validator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.TYPE;

@Documented
@Target(TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = {StudyRoomTimeValidator.class})
public @interface ValidTimeRange {
    String message() default "{开放时间不合法}";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
