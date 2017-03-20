package me.academeg.blog.api.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * BlogEntityNotExistException
 *
 * @author Yuriy A. Samsonov <y.samsonov@erpscan.com>
 * @version 1.0
 */
@ResponseStatus(value = HttpStatus.BAD_REQUEST)
public class BlogEntityNotExistException extends BlogClientException {

    public BlogEntityNotExistException(String message) {
        super(message);
    }

    public BlogEntityNotExistException(String message, Object... args) {
        super(message, args);
    }

    public BlogEntityNotExistException(Throwable cause) {
        super(cause);
    }
}
