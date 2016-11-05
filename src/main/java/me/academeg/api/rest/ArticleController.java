package me.academeg.api.rest;

import me.academeg.entity.Account;
import me.academeg.entity.Article;
import me.academeg.exceptions.AccountPermissionException;
import me.academeg.exceptions.ArticleNotExistException;
import me.academeg.exceptions.EmptyFieldException;
import me.academeg.security.Role;
import me.academeg.service.AccountService;
import me.academeg.service.ArticleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.User;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Calendar;
import java.util.TimeZone;
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
                             @RequestParam(defaultValue = "20") int size) {
        PageRequest pageRequest = new PageRequest(page, size, Sort.Direction.DESC, "creationDate");
        return articleService.getAll(pageRequest);
    }

    @RequestMapping(value = "", method = RequestMethod.POST)
    public Article create(@RequestBody Article article, @AuthenticationPrincipal User user) {
        if (article.getText() == null || article.getText().isEmpty()
                || article.getTitle() == null || article.getTitle().isEmpty()) {
            throw new EmptyFieldException("Article cannot be empty");
        }

        Account account = accountService.getByEmail(user.getUsername());
        article.setAuthor(account);
        article.setCreationDate(Calendar.getInstance(TimeZone.getTimeZone("UTC")).getTime());
        return articleService.add(article);
    }

    @RequestMapping(value = "/{uuid}", method = RequestMethod.PUT)
    public Article edit(@AuthenticationPrincipal User user, @PathVariable UUID uuid, @RequestBody Article article) {
        Article articleFromDb = articleService.getByUuid(uuid);
        if (articleFromDb == null) {
            throw new ArticleNotExistException();
        }

        if (article.getText() == null || article.getText().isEmpty()
                || article.getTitle() == null || article.getTitle().isEmpty()) {
            throw new EmptyFieldException();
        }

        Account author = articleFromDb.getAuthor();
        Account authAccount = accountService.getByEmail(user.getUsername());
        if (!authAccount.getId().equals(author.getId())) {
            throw new AccountPermissionException("You cannot to edit this article");
        }
        articleFromDb.setTitle(article.getTitle());
        articleFromDb.setText(article.getText());
        return articleService.edit(articleFromDb);
    }

    @RequestMapping(value = "/{uuid}", method = RequestMethod.DELETE)
    @ResponseStatus(value = HttpStatus.NO_CONTENT)
    public void delete(@AuthenticationPrincipal User user, @PathVariable UUID uuid) {
        Article articleFromDb = articleService.getByUuid(uuid);
        if (articleFromDb == null) {
            throw new ArticleNotExistException("Wrong UUID");
        }

        Account authAccount = accountService.getByEmail(user.getUsername());
        if (!authAccount.getAuthority().equals(Role.ROLE_MODERATOR.name())
                && !authAccount.getAuthority().equals(Role.ROLE_ADMIN.name())
                && !authAccount.getId().equals(articleFromDb.getAuthor().getId())) {
            throw new AccountPermissionException();
        }
        articleService.delete(uuid);
    }
}
