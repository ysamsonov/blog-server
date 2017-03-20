package me.academeg.blog.api.exception;

/**
 * Represents an error in the client-side(validation fails, wrong file format and etc.)
 *
 * @author Yuriy A. Samsonov <y.samsonov@erpscan.com>
 * @date 24.02.2017
 */
public class BlogClientException extends BlogException {

    public BlogClientException(String message) {
        super(message);
    }

    public BlogClientException(String message, Object... args) {
        super(message, args);
    }

    public BlogClientException(Throwable cause) {
        super(cause);
    }
}
