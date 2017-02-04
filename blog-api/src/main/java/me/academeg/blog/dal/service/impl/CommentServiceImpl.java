package me.academeg.blog.dal.service.impl;

import me.academeg.blog.dal.domain.Article;
import me.academeg.blog.dal.domain.Comment;
import me.academeg.blog.dal.repository.CommentRepository;
import me.academeg.blog.dal.service.CommentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Date;
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
    public Comment create(Comment comment) {
        comment.setCreationDate(new Date());
        return commentRepository.save(comment);
    }

    @Override
    public void delete(UUID id) {
        commentRepository.delete(id);
    }

    @Override
    public Comment getById(UUID id) {
        return commentRepository.findOne(id);
    }

    @Override
    public Comment update(Comment comment) {
        return commentRepository.save(comment);
    }

    @Override
    public Page<Comment> getPageByArticle(Pageable pageable, Article article) {
        return commentRepository.findByArticleId(pageable, article.getId());
    }
}
