package me.academeg.api.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * AccountNotExistException
 *
 * @author Yuriy A. Samsonov <y.samsonov@erpscan.com>
 * @version 1.0
 */
@ResponseStatus(value = HttpStatus.NOT_FOUND)
public class AccountNotExistException extends RuntimeException {

    public AccountNotExistException() {
    }

    public AccountNotExistException(String message) {
        super(message);
    }
}
