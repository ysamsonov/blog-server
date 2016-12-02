package me.academeg.api.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * TagExistException
 *
 * @author Yuriy A. Samsonov <y.samsonov@erpscan.com>
 * @version 1.0
 */
@ResponseStatus(value = HttpStatus.BAD_REQUEST)
public class TagExistException extends ExistException {

    public TagExistException() {
        super("Tag is already exist");
    }

    public TagExistException(String message) {
        super(message);
    }
}
