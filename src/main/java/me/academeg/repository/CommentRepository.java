package me.academeg.repository;

import me.academeg.entity.Comment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

import java.util.UUID;

/**
 * CommentRepository
 *
 * @author Yuriy A. Samsonov <y.samsonov@erpscan.com>
 * @version 1.0
 */
public interface CommentRepository extends PagingAndSortingRepository<Comment, UUID> {

    @Query("select c from Comment c where c.article.id = :articleUuid")
    Page<Comment> findByArticleUuid(Pageable pageable, @Param("articleUuid") UUID articleUuid);
}
