package me.academeg.blog.dal.validation;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.validation.ConstraintViolation;
import javax.validation.Validator;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author Yuriy A. Samsonov <y.samsonov@erpscan.com>
 * @since 18.04.2017
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class LoginValidatorTest {

    @Autowired
    private Validator validator;

    @Test
    public void nullLogin() throws Exception {
        Set<ConstraintViolation<LoginEntity>> validate = validator.validate(new LoginEntity(null));
        assertThat(validate).hasSize(1);
    }

    @Test
    public void emptyLogin() throws Exception {
        Set<ConstraintViolation<LoginEntity>> validate = validator.validate(new LoginEntity(""));
        assertThat(validate).hasSize(2);
    }

    @Test
    public void loginWithBadSymbols() throws Exception {
        Set<ConstraintViolation<LoginEntity>> validate = validator.validate(new LoginEntity("some login @ test"));
        assertThat(validate).hasSize(1);
    }

    @Test
    public void correctLogin() throws Exception {
        Set<ConstraintViolation<LoginEntity>> validate = validator.validate(new LoginEntity("correct login"));
        assertThat(validate).isEmpty();
    }


    class LoginEntity {

        @Login
        private String login;

        LoginEntity(String login) {
            this.login = login;
        }
    }
}
