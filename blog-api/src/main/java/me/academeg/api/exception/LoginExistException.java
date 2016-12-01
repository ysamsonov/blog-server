package me.academeg.api.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * LoginExistException
 *
 * @author Yuriy A. Samsonov <y.samsonov@erpscan.com>
 * @version 1.0
 */
@ResponseStatus(value = HttpStatus.BAD_REQUEST)
public class LoginExistException extends RuntimeException {

    public LoginExistException() {
    }

    public LoginExistException(String message) {
        super(message);
    }
}
