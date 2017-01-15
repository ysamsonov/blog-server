package me.academeg.api.controller;

import com.querydsl.core.BooleanBuilder;
import lombok.extern.slf4j.Slf4j;
import me.academeg.api.common.ApiResult;
import me.academeg.api.exception.AccountPermissionException;
import me.academeg.api.exception.EntityNotExistException;
import me.academeg.dal.domain.Account;
import me.academeg.dal.domain.AccountRole;
import me.academeg.dal.domain.Article;
import me.academeg.dal.domain.ArticleStatus;
import me.academeg.dal.service.AccountService;
import me.academeg.dal.service.ArticleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.User;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;
import java.util.UUID;

import static me.academeg.api.utils.ApiUtils.*;
import static me.academeg.dal.specification.ArticleSpec.*;

/**
 * ArticleController Controller
 *
 * @author Yuriy A. Samsonov <yuriy.samsonov96@gmail.com>
 * @version 1.0
 */
@RestController
@RequestMapping("/api/articles")
@Validated
@Slf4j
public class ArticleController {
    private final AccountService accountService;
    private final ArticleService articleService;
    private final Class resourceClass;

    @Autowired
    public ArticleController(ArticleService articleService, AccountService accountService) {
        this.articleService = articleService;
        this.accountService = accountService;
        this.resourceClass = Article.class;
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public ApiResult getById(
        @AuthenticationPrincipal final User user,
        @PathVariable final UUID id
    ) {
        log.info("/GET method invoked for {} id {}", resourceClass.getSimpleName(), id);

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
    public ApiResult getList(
        @RequestParam(required = false) final UUID authorId,
        @RequestParam(required = false, defaultValue = "PUBLISHED") ArticleStatus status,
        @RequestParam(required = false) final String tag,
        @RequestParam(required = false) final Integer page,
        @RequestParam(required = false) final Integer limit,
        @AuthenticationPrincipal final User user
    ) {
        log.info("/LIST method invoked for {}, authorId {}, status {}, tag {}",
            resourceClass.getSimpleName(), authorId, status, tag);

        BooleanBuilder predicateBuilder = new BooleanBuilder(withStatus(status));
        if (authorId != null) {
            predicateBuilder.and(withAuthorId(authorId));
        }

        if (tag != null) {
            predicateBuilder.and(hasTag(tag));
        }

        if (status.equals(ArticleStatus.PUBLISHED)) {
            return listResult(
                articleService.getPage(predicateBuilder.getValue(),
                    createPageRequest(limit, page, "creationDate:desc")));
        }

        if (user == null) {
            throw new AccountPermissionException("You cannot get %s articles", status);
        }

        Account authUser = accountService.getByEmail(user.getUsername());
        if (status.equals(ArticleStatus.LOCKED)
            && (
            authUser.getAuthority().equals(AccountRole.MODERATOR)
                || authUser.getAuthority().equals(AccountRole.ADMIN))
            ) {
            return listResult(
                articleService.getPage(predicateBuilder.getValue(),
                    createPageRequest(limit, page, "creationDate:desc")));
        }

        if (authorId == null) {
            throw new AccountPermissionException("You cannot get %s articles", status);
        }

        if (authUser.getId().equals(authorId)) {
            return listResult(
                articleService.getPage(predicateBuilder.getValue(),
                    createPageRequest(limit, page, "creationDate:desc")));
        }

        throw new AccountPermissionException("You cannot get %s articles", status);
    }

    @RequestMapping(value = "/search", method = RequestMethod.GET)
    public ApiResult search(
        @RequestParam(name = "q") final String query,
        @RequestParam(required = false) final Integer page,
        @RequestParam(required = false) final Integer limit
    ) {
        log.info("/SEARCH method invoked for {} query {}", resourceClass.getSimpleName(), query);
        log.info("It's temporary solution. May be very slow(");
        return
            listResult(
                articleService.getPage(
                    hasText(query),
                    createPageRequest(limit, page, "creationDate:desc")
                )
            );
    }

    @RequestMapping(value = "", method = RequestMethod.POST)
    public ApiResult create(
        @Validated @RequestBody final Article article,
        @AuthenticationPrincipal final User user
    ) {
        log.info("/ADD method invoked for {}", resourceClass.getSimpleName());
        article.setAuthor(accountService.getByEmail(user.getUsername()));
        return singleResult(articleService.create(article));
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.PUT)
    public ApiResult update(
        @AuthenticationPrincipal final User user,
        @PathVariable final UUID id,
        @Validated @RequestBody final Article article
    ) {
        log.info("/EDIT method invoked for {} id {}", resourceClass.getSimpleName(), id);

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
    public ApiResult delete(
        @AuthenticationPrincipal final User user,
        final @PathVariable UUID id
    ) {
        log.info("/DELETE invoked for {} id {}", resourceClass.getSimpleName(), id);

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
        log.info("/LOCK invoked for {} id {}", resourceClass.getSimpleName(), id);

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
        log.info("/UNLOCK invoked for {} id {}", resourceClass.getSimpleName(), id);

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
