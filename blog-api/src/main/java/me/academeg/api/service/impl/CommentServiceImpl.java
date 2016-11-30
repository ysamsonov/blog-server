package me.academeg.api.service.impl;

import me.academeg.api.entity.Comment;
import me.academeg.api.repository.CommentRepository;
import me.academeg.api.service.CommentService;
import me.academeg.api.entity.Article;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.UUID;

/**
 * CommentServiceImpl
 *
 * @author Yuriy A. Samsonov <y.samsonov@erpscan.com>
 * @version 1.0
 */
@Service
public class CommentServiceImpl implements CommentService {

    private CommentRepository commentRepository;

    @Autowired
    public CommentServiceImpl(CommentRepository commentRepository) {
        this.commentRepository = commentRepository;
    }

    @Override
    public Comment add(Comment comment) {
        return commentRepository.save(comment);
    }

    @Override
    public void delete(UUID uuid) {
        commentRepository.delete(uuid);
    }

    @Override
    public void delete(Comment comment) {
        commentRepository.delete(comment);
    }

    @Override
    public Comment getByUuid(UUID uuid) {
        return commentRepository.findOne(uuid);
    }

    @Override
    public Comment edit(Comment comment) {
        return commentRepository.save(comment);
    }

    @Override
    public Page<Comment> findByArticle(Pageable pageable, Article article) {
        return commentRepository.findByArticleUuid(pageable, article.getId());
    }
}
