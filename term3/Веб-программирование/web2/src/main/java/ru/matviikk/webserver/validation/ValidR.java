package ru.matviikk.webserver.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Constraint(validatedBy = ValidRValidator.class)
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidR {
    String message() default "Invalid value for R. Allowed values are: {1, 1.5, 2, 2.5, 3}";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
