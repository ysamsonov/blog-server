package me.academeg.api.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * ImageNotExistException
 *
 * @author Yuriy A. Samsonov <y.samsonov@erpscan.com>
 * @version 1.0
 */
@ResponseStatus(value = HttpStatus.BAD_REQUEST)
public class ImageNotExistException extends RuntimeException {

    public ImageNotExistException() {
    }

    public ImageNotExistException(String message) {
        super(message);
    }
}
