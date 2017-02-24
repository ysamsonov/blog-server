package me.academeg.blog.api.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * BlogFileFormatException
 *
 * @author Yuriy A. Samsonov <y.samsonov@erpscan.com>
 * @version 1.0
 */
@ResponseStatus(value = HttpStatus.BAD_REQUEST)
public class BlogFileFormatException extends BlogClientException {

    public BlogFileFormatException() {
        this("Wrong file format");
    }

    public BlogFileFormatException(String message) {
        super(message);
    }

    public BlogFileFormatException(String message, Object... args) {
        super(message, args);
    }

    public BlogFileFormatException(Throwable cause) {
        super(cause);
    }
}
