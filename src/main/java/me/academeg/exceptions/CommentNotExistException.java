package me.academeg.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * CommentNotExistException
 *
 * @author Yuriy A. Samsonov <y.samsonov@erpscan.com>
 * @version 1.0
 */
@ResponseStatus(value = HttpStatus.BAD_REQUEST)
public class CommentNotExistException extends RuntimeException {

    public CommentNotExistException() {
    }

    public CommentNotExistException(String message) {
        super(message);
    }
}
