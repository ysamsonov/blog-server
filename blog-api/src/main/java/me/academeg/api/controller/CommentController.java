package me.academeg.api.controller;

import me.academeg.api.entity.Account;
import me.academeg.api.entity.Comment;
import me.academeg.api.exception.entity.AccountPermissionException;
import me.academeg.api.exception.entity.ArticleNotExistException;
import me.academeg.api.exception.entity.CommentNotExistException;
import me.academeg.api.exception.entity.EmptyFieldException;
import me.academeg.api.security.Role;
import me.academeg.api.service.ArticleService;
import me.academeg.api.service.CommentService;
import me.academeg.api.utils.ApiUtils;
import me.academeg.api.common.ApiResult;
import me.academeg.api.entity.Article;
import me.academeg.api.service.AccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.User;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Calendar;
import java.util.UUID;

/**
 * CommentController
 *
 * @author Yuriy A. Samsonov <y.samsonov@erpscan.com>
 * @version 1.0
 */
@RestController
@RequestMapping("/api/comment")
@Validated
public class CommentController {

    private final CommentService commentService;
    private final ArticleService articleService;
    private final AccountService accountService;

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

    @RequestMapping(value = "/{uuid}", method = RequestMethod.GET)
    public ApiResult getById(@PathVariable final UUID uuid) {
        Comment comment = commentService.getByUuid(uuid);
        if (comment == null) {
            throw new CommentNotExistException();
        }
        return ApiUtils.singleResult(comment);
    }

    @RequestMapping(value = "", method = RequestMethod.POST)
    public ApiResult create(
            @RequestBody final Comment commentRequest,
            @AuthenticationPrincipal final User user
    ) {
        if (commentRequest.getArticle() == null
                || commentRequest.getArticle().getId() == null
                || commentRequest.getText() == null) {
            throw new EmptyFieldException();
        }

        Article article = articleService.getByUuid(commentRequest.getArticle().getId());
        if (article == null || article.getStatus() != 0) {
            throw new ArticleNotExistException();
        }

        Comment comment = new Comment();
        comment.setText(commentRequest.getText());
        comment.setCreationDate(Calendar.getInstance());
        comment.setAuthor(accountService.getByEmail(user.getUsername()));
        comment.setArticle(article);
        return ApiUtils.singleResult(commentService.add(comment));
    }

    @RequestMapping(value = "/{uuid}", method = RequestMethod.PUT)
    public ApiResult update(
            @PathVariable final UUID uuid,
            @RequestBody final Comment commentRequest,
            @AuthenticationPrincipal final User user
    ) {
        if (commentRequest.getText() == null) {
            throw new EmptyFieldException();
        }

        Comment commentFromDb = commentService.getByUuid(uuid);
        if (commentFromDb == null) {
            throw new CommentNotExistException();
        }

        Account account = accountService.getByEmail(user.getUsername());

        if (!commentFromDb.getAuthor().getId().equals(account.getId())) {
            throw new AccountPermissionException();
        }

        commentFromDb.setText(commentRequest.getText());
        return ApiUtils.singleResult(commentService.edit(commentFromDb));
    }

    @RequestMapping(value = "/{uuid}", method = RequestMethod.DELETE)
    @ResponseStatus(value = HttpStatus.NO_CONTENT)
    public void delete(
            @PathVariable final UUID uuid,
            @AuthenticationPrincipal final User user
    ) {
        Comment commentFromDb = commentService.getByUuid(uuid);
        if (commentFromDb == null) {
            throw new CommentNotExistException();
        }

        Account account = accountService.getByEmail(user.getUsername());

        if (!(commentFromDb.getAuthor().getId().equals(account.getId())
                || account.getAuthority().equals(Role.ROLE_MODERATOR.name())
                || account.getAuthority().equals(Role.ROLE_ADMIN.name()))) {
            throw new AccountPermissionException();
        }

        commentService.delete(commentFromDb);
    }
}
