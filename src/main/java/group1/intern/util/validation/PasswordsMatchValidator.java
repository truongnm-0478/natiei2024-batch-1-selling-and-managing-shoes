package group1.intern.util.validation;

import group1.intern.annotation.PasswordsMatch;
import group1.intern.bean.AccountRegistration;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;


public class PasswordsMatchValidator implements ConstraintValidator<PasswordsMatch, AccountRegistration> {

    @Override
    public boolean isValid(AccountRegistration accountRegistration, ConstraintValidatorContext context) {
        if (accountRegistration.getPassword() == null || accountRegistration.getConfirmPassword() == null) {
            return false;
        }

        return accountRegistration.getPassword().equals(accountRegistration.getConfirmPassword());
    }
}

