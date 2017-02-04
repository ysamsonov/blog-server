package me.academeg.blog.api.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * EntityExistException
 *
 * @author Yuriy A. Samsonov <y.samsonov@erpscan.com>
 * @version 1.0
 */
@ResponseStatus(value = HttpStatus.BAD_REQUEST)
public class EntityExistException extends BlogException {

    public EntityExistException(String message) {
        super(message);
    }

    public EntityExistException(String message, Object... args) {
        super(message, args);
    }

    public EntityExistException(Throwable cause) {
        super(cause);
    }
}
