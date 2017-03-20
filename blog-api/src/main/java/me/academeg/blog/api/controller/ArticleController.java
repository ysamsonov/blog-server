package me.academeg.blog.api.controller;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Predicate;
import lombok.extern.slf4j.Slf4j;
import me.academeg.blog.api.common.ApiResult;
import me.academeg.blog.api.common.ApiResultWithData;
import me.academeg.blog.api.common.CollectionResult;
import me.academeg.blog.api.exception.BlogEntityNotExistException;
import me.academeg.blog.dal.domain.Account;
import me.academeg.blog.dal.domain.Article;
import me.academeg.blog.dal.domain.ArticleStatus;
import me.academeg.blog.dal.service.ArticleService;
import me.academeg.blog.security.RoleConstants;
import me.academeg.blog.security.UserDetailsImpl;
import org.jsoup.Jsoup;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

import static me.academeg.blog.api.utils.ApiUtils.*;
import static me.academeg.blog.dal.specification.ArticleSpec.*;

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

    private final ArticleService articleService;
    private final Class resourceClass;

    @Autowired
    public ArticleController(final ArticleService articleService) {
        this.articleService = articleService;
        this.resourceClass = Article.class;
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public ApiResult getById(
        @AuthenticationPrincipal final UserDetailsImpl user,
        @PathVariable final UUID id
    ) {
        log.info("/GET method invoked for {} id {}", resourceClass.getSimpleName(), id);

        Article article = articleService.getById(id);

        if (article == null || article.getStatus().equals(ArticleStatus.PUBLISHED)) {
            return singleResult(article);
        }

        if (user == null) {
            throw new AccessDeniedException("You don\'t have rights to get article");
        }

        if (article.getAuthor().getId().equals(user.getId())) {
            return singleResult(article);
        }

        if (article.getStatus().equals(ArticleStatus.LOCKED)
            && (user.hasAuthority(RoleConstants.MODERATOR) || user.hasAuthority(RoleConstants.ADMIN))) {
            return singleResult(article);
        }

        throw new AccessDeniedException("You don\'t have rights to get article");
    }

    @RequestMapping(value = "/list", method = RequestMethod.GET)
    public ApiResult getList(
        @RequestParam(required = false) final UUID authorId,
        @RequestParam(required = false, defaultValue = "PUBLISHED") ArticleStatus status,
        @RequestParam(required = false) final String tag,
        @RequestParam(required = false) final Integer page,
        @RequestParam(required = false) final Integer limit,
        @RequestParam(required = false, defaultValue = "creationDate:desc") final String orderBy,
        @AuthenticationPrincipal final UserDetailsImpl user
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

        Pageable pageTo = createPageRequest(limit, page, orderBy);
        Predicate predicate = predicateBuilder.getValue();

        if (status.equals(ArticleStatus.PUBLISHED)) {
            return doGetList(predicate, pageTo);
        }

        if (user == null) {
            throw new AccessDeniedException("You don\'t have rights to get article");
        }

        if (status.equals(ArticleStatus.LOCKED)
            && (user.hasAuthority(RoleConstants.MODERATOR) || user.hasAuthority(RoleConstants.ADMIN))) {
            return doGetList(predicate, pageTo);
        }

        if (authorId == null) {
            throw new AccessDeniedException("You don\'t have rights to get article");
        }

        if (user.getId().equals(authorId)) {
            return doGetList(predicate, pageTo);
        }

        throw new AccessDeniedException("You don\'t have rights to get article");
    }

    @RequestMapping(value = "/search", method = RequestMethod.GET)
    public ApiResult search(
        @RequestParam(name = "q") final String query,
        @RequestParam(required = false) final Integer page,
        @RequestParam(required = false) final Integer limit,
        @RequestParam(required = false, defaultValue = "creationDate:desc") final String orderBy
    ) {
        log.info("/SEARCH method invoked for {} query {}", resourceClass.getSimpleName(), query);
        log.info("It's temporary solution. May be very slow(");
        return doGetList(hasText(query), createPageRequest(limit, page, orderBy));
    }

    @RequestMapping(value = "", method = RequestMethod.POST)
    public ApiResult create(
        @Validated @RequestBody final Article article,
        @AuthenticationPrincipal final UserDetailsImpl user
    ) {
        log.info("/CREATE method invoked for {}", resourceClass.getSimpleName());
        article.setAuthor(new Account(user.getId()));
        return singleResult(articleService.create(article));
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.PUT)
    public ApiResult update(
        @AuthenticationPrincipal final UserDetailsImpl user,
        @PathVariable final UUID id,
        @Validated @RequestBody final Article article
    ) {
        log.info("/UPDATE method invoked for {} id {}", resourceClass.getSimpleName(), id);
        Article articleFromDb = articleService.getById(id);
        if (articleFromDb == null) {
            throw new BlogEntityNotExistException("Article with id %s not exist", id);
        }

        if (articleFromDb.getAuthor() == null || !user.getId().equals(articleFromDb.getAuthor().getId())) {
            throw new AccessDeniedException("You don\'t have rights to edit article");
        }
        article.setId(articleFromDb.getId());
        return singleResult(articleService.update(article));
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    public ApiResult delete(
        @AuthenticationPrincipal final UserDetailsImpl user,
        @PathVariable final UUID id
    ) {
        log.info("/DELETE invoked for {} id {}", resourceClass.getSimpleName(), id);

        Article article = articleService.getById(id);
        if (article == null) {
            throw new BlogEntityNotExistException("Article with id %s not exist", id);
        }

        if (user.hasAuthority(RoleConstants.MODERATOR) || user.hasAuthority(RoleConstants.ADMIN)) {
            articleService.delete(article.getId());
            return okResult();
        }

        if (article.getAuthor() == null) {
            throw new AccessDeniedException("You don\'t have rights to delete article");
        }
        if (article.getAuthor().getId().equals(user.getId())) {
            articleService.delete(article.getId());
            return okResult();
        }

        throw new AccessDeniedException("You don\'t have rights to delete article");
    }

    @Secured({RoleConstants.ADMIN, RoleConstants.MODERATOR})
    @RequestMapping(value = "/{id}/lock", method = RequestMethod.GET)
    public ApiResult lock(@PathVariable final UUID id) {
        log.info("/LOCK invoked for {} id {}", resourceClass.getSimpleName(), id);
        articleService.lock(id);
        return okResult();
    }

    @Secured({RoleConstants.ADMIN, RoleConstants.MODERATOR})
    @RequestMapping(value = "/{id}/unlock", method = RequestMethod.GET)
    public ApiResult unlock(@PathVariable final UUID id) {
        log.info("/UNLOCK invoked for {} id {}", resourceClass.getSimpleName(), id);
        articleService.unlock(id);
        return okResult();
    }

    protected ApiResultWithData<CollectionResult<Article>> doGetList(Predicate predicate, Pageable pageable) {
        Page<Article> dataPage = articleService.getPage(predicate, pageable);
        doTransform(dataPage.getContent());
        return listResult(dataPage);
    }

    protected void doTransform(List<Article> articles) {
        for (Article article : articles) {
            String data = Jsoup.parse(article.getText()).text();
            int maxLength = (data.length() < 100) ? data.length() : 100;
            data = data.substring(0, maxLength);
            article.setText(data);
        }
    }
}
