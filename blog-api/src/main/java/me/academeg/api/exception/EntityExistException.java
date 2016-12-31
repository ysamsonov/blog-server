package me.academeg.api.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * EntityExistException
 *
 * @author Yuriy A. Samsonov <y.samsonov@erpscan.com>
 * @version 1.0
 */
@ResponseStatus(value = HttpStatus.BAD_REQUEST)
public class EntityExistException extends RuntimeException {

    public EntityExistException(String message) {
        super(message);
    }

    public EntityExistException(String message, Object... args) {
        super(String.format(message, args));
    }
}
