package me.academeg.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * TagNotExistException
 *
 * @author Yuriy A. Samsonov <y.samsonov@erpscan.com>
 * @version 1.0
 */
@ResponseStatus(value = HttpStatus.BAD_REQUEST)
public class TagNotExistException extends RuntimeException {

    public TagNotExistException() {
    }

    public TagNotExistException(String message) {
        super(message);
    }
}
