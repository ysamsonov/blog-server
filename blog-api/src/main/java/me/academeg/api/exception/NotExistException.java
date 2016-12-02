package me.academeg.api.exception;

/**
 * NotExistException
 *
 * @author Yuriy A. Samsonov <y.samsonov@erpscan.com>
 * @version 1.0
 */
public class NotExistException extends RuntimeException {

    public NotExistException(String message) {
        super(message);
    }
}
