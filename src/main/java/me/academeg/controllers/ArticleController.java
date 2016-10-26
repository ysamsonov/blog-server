package me.academeg.controllers;

import me.academeg.entity.Account;
import me.academeg.entity.Article;
import me.academeg.service.AccountService;
import me.academeg.service.ArticleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.User;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.sql.Timestamp;
import java.util.UUID;

/**
 * ArticleController Controller
 *
 * @author Yuriy A. Samsonov <yuriy.samsonov96@gmail.com>
 * @version 1.0
 */
@RestController
@RequestMapping("/api/article")
@Validated
public class ArticleController {

    private ArticleService articleService;
    private AccountService accountService;

    @Autowired
    public ArticleController(ArticleService articleService, AccountService accountService) {
        this.articleService = articleService;
        this.accountService = accountService;
    }

    @RequestMapping(value = "", method = RequestMethod.GET)
    public Page<Article> get(@RequestParam(defaultValue = "0") int page,
                             @RequestParam(defaultValue = "20") int count) {
        PageRequest pageRequest = new PageRequest(page, count, Sort.Direction.DESC, "timestamp");
        return articleService.getAll(pageRequest);
    }

    @RequestMapping(value = "", method = RequestMethod.POST)
    public Article create(@RequestBody Article article, @AuthenticationPrincipal User user) {
        if (article.getText() == null || article.getText().isEmpty()) {
            throw new IllegalArgumentException("Article cannot be empty");
        }

        Account account = accountService.getByEmail(user.getUsername());
        article.setAuthor(account);
        article.setTimestamp(new Timestamp(System.currentTimeMillis()));
        return articleService.add(article);
    }

    @RequestMapping(value = "", method = RequestMethod.PUT)
    public Article edit(@AuthenticationPrincipal User user, @RequestBody Article article) {

        return null;
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    public Article delete(@AuthenticationPrincipal User user, @PathVariable UUID id) {

        return null;
    }
}
