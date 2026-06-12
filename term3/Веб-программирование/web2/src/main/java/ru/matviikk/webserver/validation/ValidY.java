package ru.matviikk.webserver.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Constraint(validatedBy = ValidYValidator.class)
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidY {
    String message() default "Invalid value for Y. Allowed values are: {-2, -1.5, -1, -0.5, 0, 0.5, 1, 1.5, 2}";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
