package me.academeg.blog.dal.service.impl;

import me.academeg.blog.api.exception.BlogEntityNotExistException;
import me.academeg.blog.dal.domain.Article;
import me.academeg.blog.dal.domain.ArticleStatus;
import me.academeg.blog.dal.domain.Comment;
import me.academeg.blog.dal.repository.CommentRepository;
import me.academeg.blog.dal.service.ArticleService;
import me.academeg.blog.dal.service.CommentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Optional;
import java.util.UUID;

/**
 * CommentServiceImpl
 *
 * @author Yuriy A. Samsonov <y.samsonov@erpscan.com>
 * @version 1.0
 */
@Service
public class CommentServiceImpl implements CommentService {

    private final ArticleService articleService;
    private final CommentRepository commentRepository;

    @Autowired
    public CommentServiceImpl(
        final ArticleService articleService,
        final CommentRepository commentRepository
    ) {
        this.articleService = articleService;
        this.commentRepository = commentRepository;
    }

    @Override
    public Comment create(Comment comment) {
        if (comment.getArticle() == null || comment.getArticle().getId() == null) {
            throw new BlogEntityNotExistException("Article with nullable id not exist");
        }

        Optional
            .ofNullable(articleService.getById(comment.getArticle().getId()))
            .filter(a -> a.getStatus().equals(ArticleStatus.PUBLISHED))
            .orElseThrow(
                () -> new BlogEntityNotExistException("Article with id %s not exist", comment.getArticle().getId()));

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
