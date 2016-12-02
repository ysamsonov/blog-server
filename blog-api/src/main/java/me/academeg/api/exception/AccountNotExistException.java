package me.academeg.api.exception;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * AccountNotExistException
 *
 * @author Yuriy A. Samsonov <y.samsonov@erpscan.com>
 * @version 1.0
 */
@ResponseStatus(value = HttpStatus.NOT_FOUND)
public class AccountNotExistException extends UsernameNotFoundException {

    public AccountNotExistException() {
        super("Account not exist");
    }

    public AccountNotExistException(String message) {
        super(message);
    }
}
