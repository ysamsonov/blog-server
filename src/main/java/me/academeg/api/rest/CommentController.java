package me.academeg.api.rest;

import me.academeg.entity.Article;
import me.academeg.entity.Comment;
import me.academeg.exceptions.ArticleNotExistException;
import me.academeg.exceptions.CommentNotExistException;
import me.academeg.exceptions.EmptyFieldException;
import me.academeg.service.AccountService;
import me.academeg.service.ArticleService;
import me.academeg.service.CommentService;
import org.springframework.beans.factory.annotation.Autowired;
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
        if (article == null) {
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
        return null;
    }

    @RequestMapping(value = "/{uuid}", method = RequestMethod.DELETE)
    public void delete(@PathVariable UUID uuid,
                       @AuthenticationPrincipal User user) {

    }
}
