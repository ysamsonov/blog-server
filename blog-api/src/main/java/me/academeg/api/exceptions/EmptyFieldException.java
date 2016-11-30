package me.academeg.api.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * EmptyFieldException
 *
 * @author Yuriy A. Samsonov <y.samsonov@erpscan.com>
 * @version 1.0
 */
@ResponseStatus(value = HttpStatus.BAD_REQUEST)
public class EmptyFieldException extends RuntimeException {

    public EmptyFieldException() {
    }

    public EmptyFieldException(String message) {
        super(message);
    }
}
