package me.academeg.api.controller;

import me.academeg.api.common.ApiResult;
import me.academeg.api.entity.*;
import me.academeg.api.exception.entity.AccountPermissionException;
import me.academeg.api.exception.entity.ArticleNotExistException;
import me.academeg.api.exception.entity.CommentNotExistException;
import me.academeg.api.service.AccountService;
import me.academeg.api.service.ArticleService;
import me.academeg.api.service.CommentService;
import me.academeg.api.utils.ApiUtils;
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
import java.util.Set;
import java.util.UUID;

import static me.academeg.api.utils.ApiUtils.listResult;
import static me.academeg.api.utils.ApiUtils.okResult;

/**
 * CommentController
 *
 * @author Yuriy A. Samsonov <y.samsonov@erpscan.com>
 * @version 1.0
 */
@RestController
@RequestMapping("/api/comments")
@Validated
public class CommentController {

    private final CommentService commentService;
    private final ArticleService articleService;
    private final AccountService accountService;

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
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public ApiResult getById(@PathVariable final UUID id) {
        return ApiUtils.singleResult(commentService.getById(id));
    }

    @RequestMapping(value = "", method = RequestMethod.POST)
    public ApiResult create(
        @RequestBody @Validated final Comment comment,
        @AuthenticationPrincipal final User user
    ) {
        if (comment.getArticle().getId() == null) {
            throw new ArticleNotExistException();
        }

        Article article = articleService.getById(comment.getArticle().getId());
        if (article == null || !article.getStatus().equals(ArticleStatus.PUBLISHED)) {
            throw new ArticleNotExistException();
        }

        comment.setAuthor(accountService.getByEmail(user.getUsername()));
        return ApiUtils.singleResult(commentService.create(comment));
    }

    @RequestMapping(value = "/{uuid}", method = RequestMethod.PUT)
    public ApiResult update(
        @PathVariable final UUID uuid,
        @RequestBody final Comment commentRequest,
        @AuthenticationPrincipal final User user
    ) {
        Set<ConstraintViolation<Comment>> validated = validator.validateProperty(commentRequest, "text");
        if (validated.size() > 0) {
            throw new ConstraintViolationException(validated);
        }

        Comment commentFromDb = commentService.getById(uuid);
        if (commentFromDb == null) {
            throw new CommentNotExistException();
        }

        Account account = accountService.getByEmail(user.getUsername());
        if (!commentFromDb.getAuthor().getId().equals(account.getId())) {
            throw new AccountPermissionException();
        }

        commentFromDb.setText(commentRequest.getText());
        return ApiUtils.singleResult(commentService.update(commentFromDb));
    }

    @RequestMapping(value = "/list", method = RequestMethod.GET)
    public ApiResult getList(
        @AuthenticationPrincipal final User user,
        @RequestParam final UUID articleId,
        final Integer page,
        final Integer limit
    ) {
        Article article = articleService.getById(articleId);
        if (article == null) {
            throw new ArticleNotExistException();
        }

        Page<Comment> comments = commentService
            .getPageByArticle(ApiUtils.createPageRequest(limit, page, "creationDate:desc"), article);
        if (article.getStatus().equals(ArticleStatus.PUBLISHED)) {
            return listResult(comments);
        }

        if (user == null) {
            throw new ArticleNotExistException();
        }
        Account account = accountService.getByEmail(user.getUsername());
        if (article.getAuthor().getId().equals(account.getId())) {
            return listResult(comments);
        }

        if (article.getStatus().equals(ArticleStatus.LOCKED) && (account.getAuthority().equals(AccountRole.MODERATOR)
            || account.getAuthority().equals(AccountRole.ADMIN))) {
            return listResult(comments);
        }

        throw new ArticleNotExistException();
    }

    @RequestMapping(value = "/{uuid}", method = RequestMethod.DELETE)
    public ApiResult delete(
        @PathVariable final UUID uuid,
        @AuthenticationPrincipal final User user
    ) {
        Comment commentFromDb = commentService.getById(uuid);
        if (commentFromDb == null) {
            throw new CommentNotExistException();
        }

        Account account = accountService.getByEmail(user.getUsername());

        if (!(commentFromDb.getAuthor().getId().equals(account.getId())
            || account.getAuthority().equals(AccountRole.MODERATOR)
            || account.getAuthority().equals(AccountRole.ADMIN))) {
            throw new AccountPermissionException();
        }
        commentService.delete(commentFromDb.getId());
        return okResult();
    }
}
