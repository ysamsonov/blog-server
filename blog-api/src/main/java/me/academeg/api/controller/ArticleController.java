package me.academeg.api.controller;

import me.academeg.api.Constants;
import me.academeg.api.common.ApiResult;
import me.academeg.api.entity.*;
import me.academeg.api.exception.entity.AccountPermissionException;
import me.academeg.api.exception.entity.ArticleNotExistException;
import me.academeg.api.service.AccountService;
import me.academeg.api.service.ArticleService;
import me.academeg.api.service.ImageService;
import me.academeg.api.service.TagService;
import me.academeg.api.utils.ApiUtils;
import me.academeg.api.utils.ImageUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.User;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.UUID;

import static me.academeg.api.utils.ApiUtils.listResult;
import static me.academeg.api.utils.ApiUtils.singleResult;

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
    private final ImageService imageService;
    private final TagService tagService;

    @Autowired
    public ArticleController(
        ArticleService articleService,
        AccountService accountService,
        ImageService imageService,
        TagService tagService
    ) {
        this.articleService = articleService;
        this.accountService = accountService;
        this.imageService = imageService;
        this.tagService = tagService;
    }

    @RequestMapping(value = "/{uuid}", method = RequestMethod.GET)
    public ApiResult getById(
        @AuthenticationPrincipal final User user,
        @PathVariable final UUID uuid
    ) {
        Article article = articleService.getById(uuid);
        if (article == null) {
            throw new ArticleNotExistException();
        }

        if (article.getStatus().equals(ArticleStatus.PUBLISHED)) {
            return singleResult(article);
        }

        if (user == null) {
            throw new ArticleNotExistException();
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

        throw new ArticleNotExistException();
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
        Article saveArticle = new Article();
        saveArticle.setAuthor(accountService.getByEmail(user.getUsername()));
        saveArticle.setTitle(article.getTitle());
        saveArticle.setText(article.getText());
        if (!article.getStatus().equals(ArticleStatus.PUBLISHED) || !article.getStatus().equals(ArticleStatus.DRAFT)) {
            article.setStatus(ArticleStatus.PUBLISHED);
        }
        saveArticle.setStatus(article.getStatus());
        saveArticle.setCreationDate(new Date());
        saveArticle.setTags(new HashSet<>());
        addTagsToArticle(article.getTags(), saveArticle);

        Article articleFromDb = articleService.create(saveArticle);
        articleFromDb.setImages(new HashSet<>());
        addImagesToArticle(article, articleFromDb);
        return singleResult(articleFromDb);
    }

    @RequestMapping(value = "/{uuid}", method = RequestMethod.PUT)
    public ApiResult update(
        @AuthenticationPrincipal final User user,
        @PathVariable final UUID uuid,
        @Validated @RequestBody final Article article
    ) {
        Article articleFromDb = articleService.getById(uuid);
        if (articleFromDb == null) {
            throw new ArticleNotExistException();
        }

        Account author = articleFromDb.getAuthor();
        Account authAccount = accountService.getByEmail(user.getUsername());
        if (!authAccount.getId().equals(author.getId())) {
            throw new AccountPermissionException("You cannot to update this article");
        }
        articleFromDb.setTitle(article.getTitle());
        articleFromDb.setText(article.getText());
        if (!articleFromDb.getStatus().equals(ArticleStatus.LOCKED)) {
            if (article.getStatus() != null) {
                if (article.getStatus().equals(ArticleStatus.LOCKED)) {
                    article.setStatus(ArticleStatus.DRAFT);
                }
                articleFromDb.setStatus(article.getStatus());
            }
        }
        articleFromDb.getTags().clear();
        addTagsToArticle(article.getTags(), articleFromDb);
        addImagesToArticle(article, articleFromDb);
        return singleResult(articleService.update(articleFromDb));
    }

    @RequestMapping(value = "/{uuid}", method = RequestMethod.DELETE)
    @ResponseStatus(value = HttpStatus.NO_CONTENT)
    public void delete(@AuthenticationPrincipal final User user, final @PathVariable UUID uuid) {
        Article articleFromDb = articleService.getById(uuid);
        if (articleFromDb == null) {
            throw new ArticleNotExistException("Wrong UUID");
        }

        Account authAccount = accountService.getByEmail(user.getUsername());
        if (!authAccount.getAuthority().equals(AccountRole.MODERATOR)
            && !authAccount.getAuthority().equals(AccountRole.ADMIN)
            && !authAccount.getId().equals(articleFromDb.getAuthor().getId())) {
            throw new AccountPermissionException();
        }

        for (Image image : articleFromDb.getImages()) {
            ImageUtils.deleteImages(Constants.IMAGE_PATH, image.getThumbnailPath(), image.getOriginalPath());
        }

        articleService.delete(uuid);
    }

    @RequestMapping(value = "/{uuid}/block", method = RequestMethod.GET)
    @ResponseStatus(value = HttpStatus.NO_CONTENT)
    public void lock(@AuthenticationPrincipal final User user, @PathVariable final UUID uuid) {
        Account account = accountService.getByEmail(user.getUsername());
        if (!(account.getAuthority().equals(AccountRole.ADMIN)
            || account.getAuthority().equals(AccountRole.MODERATOR))) {
            throw new AccountPermissionException();
        }

        Article article = articleService.getById(uuid);
        if (article == null || article.getStatus().equals(ArticleStatus.DRAFT)) {
            throw new ArticleNotExistException();
        }

        article.setStatus(ArticleStatus.LOCKED);
        articleService.update(article);
    }

    @RequestMapping(value = "/{uuid}/unlock", method = RequestMethod.GET)
    @ResponseStatus(value = HttpStatus.NO_CONTENT)
    public void unlock(@AuthenticationPrincipal final User user, @PathVariable final UUID uuid) {
        Account account = accountService.getByEmail(user.getUsername());
        if (!(account.getAuthority().equals(AccountRole.ADMIN))
            || account.getAuthority().equals(AccountRole.MODERATOR)) {
            throw new AccountPermissionException();
        }

        Article article = articleService.getById(uuid);
        if (article == null || article.getStatus().equals(ArticleStatus.DRAFT)) {
            throw new ArticleNotExistException();
        }


        article.setStatus(ArticleStatus.PUBLISHED);
        articleService.update(article);
    }

    private void addImagesToArticle(Article article, Article articleFromDb) {
        if (article.getImages() != null) {
            for (Image image : article.getImages()) {
                Image imageFromDb = imageService.getByUuid(image.getId());
                if (imageFromDb != null && imageFromDb.getArticle() == null) {
                    imageFromDb.setArticle(articleFromDb);
                    imageService.update(imageFromDb);
                    articleFromDb.getImages().add(imageFromDb);
                }
            }
        }
    }

    private void addTagsToArticle(Collection<Tag> tags, Article article) {
        if (tags == null) {
            return;
        }

        for (Tag tag : tags) {
            Tag tagFromDb = tagService.getById(tag.getId());
            if (tagFromDb != null) {
                article.getTags().add(tagFromDb);
            }
        }
    }
}
