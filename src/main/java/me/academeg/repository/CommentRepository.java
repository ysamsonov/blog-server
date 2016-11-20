package me.academeg.repository;

import me.academeg.entity.Comment;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.UUID;

/**
 * CommentRepository
 *
 * @author Yuriy A. Samsonov <y.samsonov@erpscan.com>
 * @version 1.0
 */
public interface CommentRepository extends PagingAndSortingRepository<Comment, UUID> {
}
