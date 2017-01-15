package me.academeg.dal.repository;

import me.academeg.dal.domain.Comment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.UUID;

/**
 * CommentRepository
 *
 * @author Yuriy A. Samsonov <y.samsonov@erpscan.com>
 * @version 1.0
 */
public interface CommentRepository extends BaseRepository<Comment, UUID> {

    @Query("select c from Comment c where c.article.id = :articleId")
    Page<Comment> findByArticleId(Pageable pageable, @Param("articleId") UUID articleId);
}
