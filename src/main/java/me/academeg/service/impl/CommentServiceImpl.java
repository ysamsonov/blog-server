package me.academeg.service.impl;

import me.academeg.entity.Article;
import me.academeg.entity.Comment;
import me.academeg.repository.CommentRepository;
import me.academeg.service.CommentService;
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
