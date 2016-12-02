package me.academeg.api.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * TagNotExistException
 *
 * @author Yuriy A. Samsonov <y.samsonov@erpscan.com>
 * @version 1.0
 */
@ResponseStatus(value = HttpStatus.BAD_REQUEST)
public class TagNotExistException extends NotExistException {

    public TagNotExistException() {
        super("Tag not exist");
    }

    public TagNotExistException(String message) {
        super(message);
    }
}
