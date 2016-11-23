package me.academeg.service;

import me.academeg.entity.Article;
import me.academeg.entity.Comment;
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
