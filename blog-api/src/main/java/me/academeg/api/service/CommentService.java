package me.academeg.api.service;

import me.academeg.api.entity.Article;
import me.academeg.api.entity.Comment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

/**
 * CommentService
 *
 * @author Yuriy A. Samsonov <y.samsonov@erpscan.com>
 * @version 1.0
 */
public interface CommentService {

    Comment create(Comment comment);

    void delete(UUID id);

    Comment getById(UUID id);

    Page<Comment> getPageByArticle(Pageable pageable, Article article);

    Comment update(Comment comment);
}
