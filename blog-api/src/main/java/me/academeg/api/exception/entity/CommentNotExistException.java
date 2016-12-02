package me.academeg.api.exception.entity;

import me.academeg.api.exception.EntityNotExistException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * CommentNotExistException
 *
 * @author Yuriy A. Samsonov <y.samsonov@erpscan.com>
 * @version 1.0
 */
@ResponseStatus(value = HttpStatus.BAD_REQUEST)
public class CommentNotExistException extends EntityNotExistException {

    public CommentNotExistException() {
        super("Comment not exist exception");
    }

    public CommentNotExistException(String message) {
        super(message);
    }
}
