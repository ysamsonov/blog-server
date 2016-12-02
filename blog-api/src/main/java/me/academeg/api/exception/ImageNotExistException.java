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
public class ImageNotExistException extends NotExistException {

    public ImageNotExistException() {
        super("Image not exist");
    }

    public ImageNotExistException(String message) {
        super(message);
    }
}
