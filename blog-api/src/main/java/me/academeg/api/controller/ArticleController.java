package me.academeg.api.controller;

import me.academeg.api.common.ApiResult;
import me.academeg.api.entity.*;
import me.academeg.api.exception.AccountPermissionException;
import me.academeg.api.exception.EntityNotExistException;
import me.academeg.api.service.AccountService;
import me.academeg.api.service.ArticleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.User;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Fetch;
import javax.persistence.criteria.Root;
import java.util.List;
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

    @PersistenceContext
    private EntityManager entityManager;

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

        CriteriaBuilder builder = entityManager.getCriteriaBuilder();

        CriteriaQuery<Article> criteriaQuery = builder.createQuery(Article.class);
        Root<Article> articleRoot = criteriaQuery.from(Article.class);
        criteriaQuery.select(articleRoot);

        Specification<Article> titleSpec = (root, query, cb) -> cb
            .like(root.get(Article_.title), "%#123");

        Specification<Article> tagsSpec = (root, query, cb) -> {
            Fetch<Article, Tag> articleTag = root.fetch(Article_.tags);
            Root<Tag> tagRoot = query.from(Tag.class);
            return cb.like(tagRoot.get(Tag_.value), "фотки");
        };

        criteriaQuery.where(builder.and(
//            titleSpec.toPredicate(articleRoot, criteriaQuery, builder),
            tagsSpec.toPredicate(articleRoot, criteriaQuery, builder)
        ));

        criteriaQuery.orderBy(builder.desc(articleRoot.get(Article_.creationDate)));
        criteriaQuery.distinct(true);

        TypedQuery<Article> typedQuery = entityManager.createQuery(criteriaQuery);
        typedQuery.setFirstResult(0); // page * limit
        typedQuery.setMaxResults(20); // limit
        return listResult(typedQuery.getResultList());
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
