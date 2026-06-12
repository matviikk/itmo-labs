package ru.matviikk.webserver.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.Arrays;
import java.util.List;

public class ValidRValidator implements ConstraintValidator<ValidR, Double> {

    private final List<Double> validRValues = Arrays.asList(1.0, 1.5, 2.0, 2.5, 3.0);

    @Override
    public boolean isValid(Double value, ConstraintValidatorContext context) {
        if (value == null) {
            return false;
        }
        return validRValues.contains(value);
    }
}
