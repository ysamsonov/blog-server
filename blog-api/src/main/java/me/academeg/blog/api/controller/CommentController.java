package me.academeg.blog.api.controller;

import lombok.extern.slf4j.Slf4j;
import me.academeg.blog.api.common.ApiResult;
import me.academeg.blog.api.exception.EntityNotExistException;
import me.academeg.blog.dal.domain.*;
import me.academeg.blog.dal.service.AccountService;
import me.academeg.blog.api.exception.AccountPermissionException;
import me.academeg.blog.dal.service.ArticleService;
import me.academeg.blog.dal.service.CommentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.User;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.validation.Validation;
import javax.validation.Validator;
import java.util.Optional;
import java.util.Set;
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
    private final ArticleService articleService;
    private final AccountService accountService;
    private final Class resourceClass;

    private final Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

    @Autowired
    public CommentController(
        CommentService commentService,
        ArticleService articleService,
        AccountService accountService
    ) {
        this.commentService = commentService;
        this.articleService = articleService;
        this.accountService = accountService;
        this.resourceClass = Comment.class;
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public ApiResult getById(@PathVariable final UUID id) {
        log.info("/GET method invoked for {} id {}", resourceClass.getSimpleName(), id);
        //noinspection RedundantTypeArguments
        return singleResult(
            Optional
                .ofNullable(commentService.getById(id))
                .<EntityNotExistException>orElseThrow(
                    () -> new EntityNotExistException(String.format("Comment with id %s not exist", id)))
        );
    }

    @RequestMapping(value = "", method = RequestMethod.POST)
    public ApiResult create(
        @RequestBody @Validated final Comment comment,
        @AuthenticationPrincipal final User user
    ) {
        log.info("/CREATE method invoked for {}", resourceClass.getSimpleName());
        if (comment.getArticle().getId() == null) {
            throw new EntityNotExistException("Article with nullable id not exist");
        }

        Optional
            .ofNullable(articleService.getById(comment.getArticle().getId()))
            .filter(a -> a.getStatus().equals(ArticleStatus.PUBLISHED))
            .orElseThrow(
                () -> new EntityNotExistException("Article with id %s not exist", comment.getArticle().getId()));

        comment.setAuthor(accountService.getByEmail(user.getUsername()));
        return singleResult(commentService.create(comment));
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.PUT)
    public ApiResult update(
        @PathVariable final UUID id,
        @RequestBody final Comment commentRequest,
        @AuthenticationPrincipal final User user
    ) {
        log.info("/UPDATE method invoked for {} id {}", resourceClass.getSimpleName(), id);
        Set<ConstraintViolation<Comment>> validated = validator.validateProperty(commentRequest, "text");
        if (validated.size() > 0) {
            throw new ConstraintViolationException(validated);
        }

        Comment commentFromDb = Optional
            .ofNullable(commentService.getById(id))
            .orElseThrow(() -> new EntityNotExistException(String.format("Comment with id %s not exist", id)));

        Account authAccount = accountService.getByEmail(user.getUsername());
        if (commentFromDb.getAuthor() == null || !authAccount.getId().equals(commentFromDb.getAuthor().getId())) {
            throw new AccountPermissionException();
        }

        commentFromDb.setText(commentRequest.getText());
        return singleResult(commentService.update(commentFromDb));
    }

    @RequestMapping(value = "/list", method = RequestMethod.GET)
    public ApiResult getList(
        @AuthenticationPrincipal final User user,
        @RequestParam final UUID articleId,
        final Integer page,
        final Integer limit
    ) {
        log.info("/LIST method invoked for {} articleId {}", resourceClass.getSimpleName(), articleId);
        Article article = articleService.getById(articleId);
        if (article == null) {
            throw new EntityNotExistException("Article with id %s not exist", articleId);
        }

        Page<Comment> comments = commentService
            .getPageByArticle(createPageRequest(limit, page, "creationDate:desc"), article);
        if (article.getStatus().equals(ArticleStatus.PUBLISHED)) {
            return listResult(comments);
        }

        if (user == null) {
            throw new EntityNotExistException("Article with id %s not exist", articleId);
        }
        Account account = accountService.getByEmail(user.getUsername());
        if (article.getAuthor().getId().equals(account.getId())) {
            return listResult(comments);
        }

        if (article.getStatus().equals(ArticleStatus.LOCKED) && (account.hasRole(AccountRole.MODERATOR)
            || account.hasRole(AccountRole.ADMIN))) {
            return listResult(comments);
        }

        throw new EntityNotExistException("Article with id %s not exist", articleId);
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    public ApiResult delete(
        @PathVariable final UUID id,
        @AuthenticationPrincipal final User user
    ) {
        log.info("/DELETE method invoked for {} id {}", resourceClass.getSimpleName(), id);
        Comment comment = Optional
            .ofNullable(commentService.getById(id))
            .orElseThrow(() -> new EntityNotExistException(String.format("Comment with id %s not exist", id)));

        Account account = accountService.getByEmail(user.getUsername());
        if (account.hasRole(AccountRole.ADMIN)
            || account.hasRole(AccountRole.MODERATOR)) {
            commentService.delete(comment.getId());
            return okResult();
        }
        if (comment.getAuthor() == null) {
            throw new AccountPermissionException();
        }
        if (comment.getAuthor().getId().equals(account.getId())) {
            commentService.delete(comment.getId());
            return okResult();
        }

        throw new AccountPermissionException();
    }
}
