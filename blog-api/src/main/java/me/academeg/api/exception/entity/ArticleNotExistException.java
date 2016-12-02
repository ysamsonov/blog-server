package me.academeg.api.exception.entity;

import me.academeg.api.exception.EntityNotExistException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * ArticleNotExistException
 *
 * @author Yuriy A. Samsonov <y.samsonov@erpscan.com>
 * @version 1.0
 */
@ResponseStatus(value = HttpStatus.BAD_REQUEST)
public class ArticleNotExistException extends EntityNotExistException {

    public ArticleNotExistException() {
        super("Article not exist");
    }

    public ArticleNotExistException(String message) {
        super(message);
    }
}
