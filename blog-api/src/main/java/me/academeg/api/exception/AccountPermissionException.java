package me.academeg.api.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * AccountPermissionException
 *
 * @author Yuriy A. Samsonov <y.samsonov@erpscan.com>
 * @version 1.0
 */
@ResponseStatus(value = HttpStatus.FORBIDDEN)
public class AccountPermissionException extends RuntimeException {

    public AccountPermissionException() {
    }

    public AccountPermissionException(String message) {
        super(message);
    }
}
