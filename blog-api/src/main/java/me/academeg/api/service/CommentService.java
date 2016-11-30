package me.academeg.api.service;

import me.academeg.api.entity.Comment;
import me.academeg.api.entity.Article;
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

    Comment add(Comment comment);

    void delete(UUID uuid);

    void delete(Comment comment);

    Comment getByUuid(UUID uuid);

    Comment edit(Comment comment);

    Page<Comment> findByArticle(Pageable pageable, Article article);
}
