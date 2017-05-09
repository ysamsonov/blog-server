package me.academeg.blog.dal.validation;

import org.hibernate.validator.constraints.NotBlank;

import javax.validation.Constraint;
import javax.validation.Payload;
import javax.validation.constraints.Size;
import java.lang.annotation.*;

/**
 * Annotation to validate login
 * Login must have size more than 3
 * Login must not contain '@' symbol
 *
 * @author Yuriy A. Samsonov <y.samsonov@erpscan.com>
 * @since 18.04.2017
 */
@Size(min = 3)
@NotBlank
@Constraint(validatedBy = LoginValidator.class)
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Login {

    String message() default "{me.academeg.blog.dal.validation.Login.message}";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
