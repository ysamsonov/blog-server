package me.academeg.blog.dal.validation;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

/**
 * Provide logic to validate {@link Login} annotation
 *
 * @author Yuriy A. Samsonov <y.samsonov@erpscan.com>
 * @since 18.04.2017
 */
public class LoginValidator implements ConstraintValidator<Login, String> {

    @Override
    public void initialize(Login constraint) {
    }

    @Override
    public boolean isValid(String login, ConstraintValidatorContext context) {
        if (login == null || "".equals(login)) {
            return true;
        }

        return !login.contains("@");
    }
}
