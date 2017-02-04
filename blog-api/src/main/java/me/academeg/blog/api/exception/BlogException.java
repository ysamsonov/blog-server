package me.academeg.blog.api.exception;

/**
 * Base class for Blog exceptions
 *
 * @author Yuriy A. Samsonov <y.samsonov@erpscan.com>
 * @version 1.0
 * @date 04.02.2017
 */
public class BlogException extends RuntimeException {

    public BlogException(String message) {
        super(message);
    }

    public BlogException(String message, Object... args) {
        super(String.format(message, args));
    }

    public BlogException(Throwable cause) {
        super(cause);
    }
}
