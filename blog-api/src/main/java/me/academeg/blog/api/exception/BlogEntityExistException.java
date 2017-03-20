package me.academeg.blog.api.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * BlogEntityExistException
 *
 * @author Yuriy A. Samsonov <y.samsonov@erpscan.com>
 * @version 1.0
 */
@ResponseStatus(value = HttpStatus.BAD_REQUEST)
public class BlogEntityExistException extends BlogClientException {

    public BlogEntityExistException(String message) {
        super(message);
    }

    public BlogEntityExistException(String message, Object... args) {
        super(message, args);
    }

    public BlogEntityExistException(Throwable cause) {
        super(cause);
    }
}
