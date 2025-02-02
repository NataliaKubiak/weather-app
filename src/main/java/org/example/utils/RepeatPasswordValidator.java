package org.example.utils;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.example.entities.dto.NewUserDto;

public class RepeatPasswordValidator implements ConstraintValidator<ValidRepeatPassword, NewUserDto> {

    @Override
    public boolean isValid(NewUserDto newUserDto, ConstraintValidatorContext context) {

        if (newUserDto == null) {
            return true;
        }

        String password = newUserDto.getPassword();
        String repeatPassword = newUserDto.getRepeatPassword();

        if (password == null || !password.equals(repeatPassword)) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate("Passwords don't match")
                    .addPropertyNode("repeatPassword")
                    .addConstraintViolation();
            return false;
        }

        return true;
    }
}
