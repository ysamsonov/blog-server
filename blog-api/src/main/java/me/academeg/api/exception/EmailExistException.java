package me.academeg.api.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * EmailExistException
 *
 * @author Yuriy A. Samsonov <y.samsonov@erpscan.com>
 * @version 1.0
 */
@ResponseStatus(value = HttpStatus.BAD_REQUEST)
public class EmailExistException extends ExistException {

    public EmailExistException() {
        super("Email is already exist");
    }

    public EmailExistException(String message) {
        super(message);
    }
}
