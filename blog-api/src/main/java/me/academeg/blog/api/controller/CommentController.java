package me.academeg.blog.api.controller;

import lombok.extern.slf4j.Slf4j;
import me.academeg.blog.api.common.ApiResult;
import me.academeg.blog.api.exception.BlogEntityNotExistException;
import me.academeg.blog.dal.domain.Account;
import me.academeg.blog.dal.domain.Article;
import me.academeg.blog.dal.domain.ArticleStatus;
import me.academeg.blog.dal.domain.Comment;
import me.academeg.blog.dal.service.CommentService;
import me.academeg.blog.security.RoleConstants;
import me.academeg.blog.security.UserDetailsImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;
import java.util.UUID;

import static me.academeg.blog.api.utils.ApiUtils.*;

/**
 * CommentController
 *
 * @author Yuriy A. Samsonov <y.samsonov@erpscan.com>
 * @version 1.0
 */
@RestController
@RequestMapping("/api/comments")
@Validated
@Slf4j
public class CommentController {

    private final CommentService commentService;
    private final Class resourceClass;

    @Autowired
    public CommentController(final CommentService commentService) {
        this.commentService = commentService;
        this.resourceClass = Comment.class;
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public ApiResult getById(@PathVariable final UUID id) {
        log.info("/GET method invoked for {} id {}", resourceClass.getSimpleName(), id);
        return singleResult(commentService.getById(id));
    }

    @RequestMapping(value = "", method = RequestMethod.POST)
    public ApiResult create(
        @RequestBody @Validated final Comment comment,
        @AuthenticationPrincipal final UserDetailsImpl user
    ) {
        log.info("/CREATE method invoked for {}", resourceClass.getSimpleName());
        comment.setAuthor(new Account(user.getId()));
        return singleResult(commentService.create(comment));
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.PUT)
    public ApiResult update(
        @PathVariable final UUID id,
        @RequestBody final Comment commentRequest,
        @AuthenticationPrincipal final UserDetailsImpl user
    ) {
        log.info("/UPDATE method invoked for {} id {}", resourceClass.getSimpleName(), id);

        Comment commentFromDb = Optional
            .ofNullable(commentService.getById(id))
            .orElseThrow(() -> new BlogEntityNotExistException(String.format("Comment with id %s not exist", id)));

        if (commentFromDb.getAuthor() == null || !user.getId().equals(commentFromDb.getAuthor().getId())) {
            throw new AccessDeniedException("You don't have rights to edit comment");
        }

        commentFromDb.setText(commentRequest.getText());
        return singleResult(commentService.update(commentFromDb));
    }

    @RequestMapping(value = "/list", method = RequestMethod.GET)
    public ApiResult getList(
        @AuthenticationPrincipal final UserDetailsImpl user,
        @RequestParam final UUID articleId,
        @RequestParam(required = false) final Integer page,
        @RequestParam(required = false) final Integer limit,
        @RequestParam(required = false, value = "creationDate:desc") final String orderBy
    ) {
        log.info("/LIST method invoked for {} articleId {}", resourceClass.getSimpleName(), articleId);
        Page<Comment> comments = commentService.getPageByArticle(
            createPageRequest(limit, page, orderBy),
            new Article(articleId)
        );

        if (comments.getContent().size() == 0) {
            return listResult(comments);
        }

        Article article = comments.getContent().get(0).getArticle();
        if (article.getStatus().equals(ArticleStatus.PUBLISHED)) {
            return listResult(comments);
        }

        if (user == null) {
            throw new AccessDeniedException("You don't have rights to get comments");
        }

        if (article.getAuthor().getId().equals(user.getId())) {
            return listResult(comments);
        }

        if (article.getStatus().equals(ArticleStatus.LOCKED)
            && (user.hasAuthority(RoleConstants.MODERATOR) || user.hasAuthority(RoleConstants.ADMIN))) {
            return listResult(comments);
        }

        throw new AccessDeniedException("You don't have rights to get comments");
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    public ApiResult delete(
        @PathVariable final UUID id,
        @AuthenticationPrincipal final UserDetailsImpl user
    ) {
        log.info("/DELETE method invoked for {} id {}", resourceClass.getSimpleName(), id);
        Comment comment = Optional
            .ofNullable(commentService.getById(id))
            .orElseThrow(() -> new BlogEntityNotExistException(String.format("Comment with id %s not exist", id)));

        if (user.hasAuthority(RoleConstants.MODERATOR) || user.hasAuthority(RoleConstants.ADMIN)) {
            commentService.delete(comment.getId());
            return okResult();
        }

        if (comment.getAuthor() == null) {
            throw new AccessDeniedException("You don't have rights to delete comment");
        }

        if (comment.getAuthor().getId().equals(user.getId())) {
            commentService.delete(comment.getId());
            return okResult();
        }

        throw new AccessDeniedException("You don't have rights to delete comment");
    }
}
