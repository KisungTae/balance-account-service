package com.beeswork.balanceaccountservice.validator;

import com.beeswork.balanceaccountservice.constant.RegexExpression;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.UUID;

public class UUIDValidator implements ConstraintValidator<ValidUUID, UUID> {

    @Override
    public void initialize(ValidUUID validUuid) { }

    @Override
    public boolean isValid(UUID uuid, ConstraintValidatorContext constraintValidatorContext) {
        if (uuid == null) {
            return false;
        }
        return uuid.toString().matches(RegexExpression.VALID_UUID);
    }
}
