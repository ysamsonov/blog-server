package me.academeg.api.controller;

import me.academeg.api.common.ApiResult;
import me.academeg.api.entity.Account;
import me.academeg.api.entity.AccountRole;
import me.academeg.api.entity.Article;
import me.academeg.api.entity.ArticleStatus;
import me.academeg.api.exception.EntityNotExistException;
import me.academeg.api.exception.entity.AccountPermissionException;
import me.academeg.api.service.AccountService;
import me.academeg.api.service.ArticleService;
import me.academeg.api.utils.ApiUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.User;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;
import java.util.UUID;

import static me.academeg.api.utils.ApiUtils.*;

/**
 * ArticleController Controller
 *
 * @author Yuriy A. Samsonov <yuriy.samsonov96@gmail.com>
 * @version 1.0
 */
@RestController
@RequestMapping("/api/articles")
@Validated
public class ArticleController {
    private final AccountService accountService;
    private final ArticleService articleService;

    @Autowired
    public ArticleController(ArticleService articleService, AccountService accountService) {
        this.articleService = articleService;
        this.accountService = accountService;
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public ApiResult getById(
        @AuthenticationPrincipal final User user,
        @PathVariable final UUID id
    ) {
        Article article = Optional
            .ofNullable(articleService.getById(id))
            .orElseThrow(() -> new EntityNotExistException("Article with id %s not exist", id));

        if (article.getStatus().equals(ArticleStatus.PUBLISHED)) {
            return singleResult(article);
        }

        if (user == null) {
            throw new EntityNotExistException("Article with id %s not exist", id);
        }

        Account account = accountService.getByEmail(user.getUsername());
        if (article.getAuthor().getId().equals(account.getId())) {
            return singleResult(article);
        }

        if (article.getStatus().equals(ArticleStatus.LOCKED)
            && (account.getAuthority().equals(AccountRole.MODERATOR)
            || account.getAuthority().equals(AccountRole.ADMIN))) {
            return singleResult(article);
        }

        throw new EntityNotExistException("Article with id %s not exist", id);
    }

    @RequestMapping(value = "/list", method = RequestMethod.GET)
    public ApiResult getList(final Integer page, final Integer limit) {
        //@TODO return not only publish article, add opportunity to filter article by status
        return listResult(articleService.getPage(ApiUtils.createPageRequest(limit, page, "creationDate:desc")));
    }

    @RequestMapping(value = "", method = RequestMethod.POST)
    public ApiResult create(
        @Validated @RequestBody final Article article,
        @AuthenticationPrincipal final User user
    ) {
        article.setAuthor(accountService.getByEmail(user.getUsername()));
        return singleResult(articleService.create(article));
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.PUT)
    public ApiResult update(
        @AuthenticationPrincipal final User user,
        @PathVariable final UUID id,
        @Validated @RequestBody final Article article
    ) {
        Article articleFromDb = articleService.getById(id);
        if (articleFromDb == null) {
            throw new EntityNotExistException("Article with id %s not exist", id);
        }

        Account authAccount = accountService.getByEmail(user.getUsername());
        if (articleFromDb.getAuthor() == null || !authAccount.getId().equals(articleFromDb.getAuthor().getId())) {
            throw new AccountPermissionException("Only author can update article");
        }
        article.setId(articleFromDb.getId());
        return singleResult(articleService.update(article));
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    public ApiResult delete(@AuthenticationPrincipal final User user, final @PathVariable UUID id) {
        Article article = articleService.getById(id);
        if (article == null) {
            throw new EntityNotExistException("Article with id %s not exist", id);
        }

        Account account = accountService.getByEmail(user.getUsername());
        if (account.getAuthority().equals(AccountRole.ADMIN)
            || account.getAuthority().equals(AccountRole.MODERATOR)) {
            articleService.delete(article.getId());
            return okResult();
        }
        if (article.getAuthor() == null) {
            throw new AccountPermissionException("Only author can update article");
        }
        if (article.getAuthor().getId().equals(account.getId())) {
            articleService.delete(article.getId());
            return okResult();
        }

        throw new AccountPermissionException();
    }

    @RequestMapping(value = "/{id}/lock", method = RequestMethod.GET)
    public ApiResult lock(@AuthenticationPrincipal final User user, @PathVariable final UUID id) {
        Account account = accountService.getByEmail(user.getUsername());
        if (!(account.getAuthority().equals(AccountRole.ADMIN)
            || account.getAuthority().equals(AccountRole.MODERATOR))) {
            throw new AccountPermissionException("Only admin/moderator can lock article");
        }

        Article article = articleService.getById(id);
        if (article == null || article.getStatus().equals(ArticleStatus.DRAFT)) {
            throw new EntityNotExistException("Article with id %s not exist", id);
        }

        articleService.lock(article);
        return okResult();
    }

    @RequestMapping(value = "/{id}/unlock", method = RequestMethod.GET)
    public ApiResult unlock(@AuthenticationPrincipal final User user, @PathVariable final UUID id) {
        Account account = accountService.getByEmail(user.getUsername());
        if (!(account.getAuthority().equals(AccountRole.ADMIN))
            || account.getAuthority().equals(AccountRole.MODERATOR)) {
            throw new AccountPermissionException("Only admin/moderator can unlock article");
        }

        Article article = articleService.getById(id);
        if (article == null || article.getStatus().equals(ArticleStatus.DRAFT)) {
            throw new EntityNotExistException("Article with id %s not exist", id);
        }

        articleService.unlock(article);
        return okResult();
    }
}
