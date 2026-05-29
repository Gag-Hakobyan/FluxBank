package com.fluxbank.validation;

import com.fluxbank.annotation.Adult;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.time.LocalDate;

public class AdultValidator implements ConstraintValidator<Adult, LocalDate> {
    @Override
    public boolean isValid(LocalDate birthDate, ConstraintValidatorContext constraintValidatorContext) {
       if (birthDate == null) {
           return true;
       }

        return birthDate.isBefore(LocalDate.now().minusYears(18))
                || birthDate.equals(LocalDate.now().minusYears(18));
    }
}
