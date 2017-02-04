package me.academeg.blog.api.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * EntityNotExistException
 *
 * @author Yuriy A. Samsonov <y.samsonov@erpscan.com>
 * @version 1.0
 */
@ResponseStatus(value = HttpStatus.BAD_REQUEST)
public class EntityNotExistException extends BlogException {

    public EntityNotExistException(String message) {
        super(message);
    }

    public EntityNotExistException(String message, Object... args) {
        super(message, args);
    }

    public EntityNotExistException(Throwable cause) {
        super(cause);
    }
}
