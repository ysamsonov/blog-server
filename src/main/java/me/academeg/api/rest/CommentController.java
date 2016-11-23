package me.academeg.api.rest;

import me.academeg.entity.Account;
import me.academeg.entity.Article;
import me.academeg.entity.Comment;
import me.academeg.exceptions.AccountPermissionException;
import me.academeg.exceptions.ArticleNotExistException;
import me.academeg.exceptions.CommentNotExistException;
import me.academeg.exceptions.EmptyFieldException;
import me.academeg.security.Role;
import me.academeg.service.AccountService;
import me.academeg.service.ArticleService;
import me.academeg.service.CommentService;
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

    private CommentService commentService;
    private ArticleService articleService;
    private AccountService accountService;

    @Autowired
    public CommentController(CommentService commentService, ArticleService articleService, AccountService accountService) {
        this.commentService = commentService;
        this.articleService = articleService;
        this.accountService = accountService;
    }

    @RequestMapping(value = "/{uuid}", method = RequestMethod.GET)
    public Comment getByUuid(@PathVariable UUID uuid) {
        Comment comment = commentService.getByUuid(uuid);
        if (comment == null) {
            throw new CommentNotExistException();
        }
        return comment;
    }

    @RequestMapping(value = "", method = RequestMethod.POST)
    public Comment create(@RequestBody Comment commentRequest,
                          @AuthenticationPrincipal User user) {
        System.out.println(commentRequest);
        if (commentRequest.getArticle() == null || commentRequest.getArticle().getId() == null || commentRequest.getText() == null) {
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
        return commentService.add(comment);
    }

    @RequestMapping(value = "/{uuid}", method = RequestMethod.PUT)
    public Comment edit(@PathVariable UUID uuid,
                        @RequestBody Comment commentRequest,
                        @AuthenticationPrincipal User user) {

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
        return commentService.edit(commentFromDb);
    }

    @RequestMapping(value = "/{uuid}", method = RequestMethod.DELETE)
    @ResponseStatus(value = HttpStatus.NO_CONTENT)
    public void delete(@PathVariable UUID uuid,
                       @AuthenticationPrincipal User user) {

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
