package me.academeg.api.exception;

/**
 * ExistException
 *
 * @author Yuriy A. Samsonov <y.samsonov@erpscan.com>
 * @version 1.0
 */
public class ExistException extends RuntimeException {

    public ExistException(String message) {
        super(message);
    }
}
