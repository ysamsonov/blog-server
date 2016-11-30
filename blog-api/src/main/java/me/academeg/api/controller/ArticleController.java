package me.academeg.api.controller;

import me.academeg.api.entity.*;
import me.academeg.api.exceptions.EmptyFieldException;
import me.academeg.api.common.ApiResult;
import me.academeg.api.service.*;
import me.academeg.api.exceptions.AccountPermissionException;
import me.academeg.api.exceptions.ArticleNotExistException;
import me.academeg.api.security.Role;
import me.academeg.api.utils.ApiUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.User;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.io.File;
import java.util.Calendar;
import java.util.Collection;
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
@RequestMapping("/api/article")
@Validated
public class ArticleController {

    private final ArticleService articleService;
    private final AccountService accountService;
    private final ImageService imageService;
    private final CommentService commentService;
    private final TagService tagService;

    @Autowired
    public ArticleController(
            ArticleService articleService,
            AccountService accountService,
            ImageService imageService,
            CommentService commentService,
            TagService tagService
    ) {
        this.articleService = articleService;
        this.accountService = accountService;
        this.imageService = imageService;
        this.commentService = commentService;
        this.tagService = tagService;
    }

    @RequestMapping(value = "/{uuid}", method = RequestMethod.GET)
    public ApiResult getById(@AuthenticationPrincipal final User user, @PathVariable final UUID uuid) {
        Article article = articleService.getByUuid(uuid);
        if (article == null) {
            throw new ArticleNotExistException();
        }

        if (article.getStatus() == 0) {
            return singleResult(article);
        }

        if (user == null) {
            throw new ArticleNotExistException();
        }

        Account account = accountService.getByEmail(user.getUsername());
        if (article.getAuthor().getId().equals(account.getId())) {
            return singleResult(article);
        }

        if (article.getStatus() == 2 && (account.getAuthority().equals(Role.ROLE_MODERATOR.name())
                || account.getAuthority().equals(Role.ROLE_ADMIN.name()))) {
            return singleResult(article);
        }

        throw new ArticleNotExistException();
    }

    @RequestMapping(value = "", method = RequestMethod.GET)
    public ApiResult getList(final Integer page, final Integer limit) {
        return listResult(articleService.getAll(ApiUtils.createPageRequest(limit, page, "creationDate:desc")));
    }

    @RequestMapping(value = "", method = RequestMethod.POST)
    public ApiResult create(@RequestBody final Article article, @AuthenticationPrincipal final User user) {
        if (article.getText() == null || article.getText().isEmpty()
                || article.getTitle() == null || article.getTitle().isEmpty()) {
            throw new EmptyFieldException("Article cannot be empty");
        }

        Article saveArticle = new Article();
        saveArticle.setAuthor(accountService.getByEmail(user.getUsername()));
        saveArticle.setTitle(article.getTitle());
        saveArticle.setText(article.getText());
        if (article.getStatus() > 1) {
            article.setStatus(1);
        }
        saveArticle.setStatus(article.getStatus());
        saveArticle.setCreationDate(Calendar.getInstance());
        saveArticle.setTags(new HashSet<>());
        addTagsToArticle(article.getTags(), saveArticle);

        Article articleFromDb = articleService.add(saveArticle);
        articleFromDb.setImages(new HashSet<>());
        addImagesToArticle(article, articleFromDb);
        return singleResult(articleFromDb);
    }

    @RequestMapping(value = "/{uuid}", method = RequestMethod.PUT)
    public ApiResult update(
            @AuthenticationPrincipal final User user,
            @PathVariable final UUID uuid,
            @RequestBody final Article article
    ) {
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
            throw new AccountPermissionException("You cannot to update this article");
        }
        articleFromDb.setTitle(article.getTitle());
        articleFromDb.setText(article.getText());
        if ((articleFromDb.getStatus() != 2)) {
            if (article.getStatus() > 1) {
                article.setStatus(1);
            }
            articleFromDb.setStatus(article.getStatus());
        }
        articleFromDb.getTags().clear();
        addTagsToArticle(article.getTags(), articleFromDb);
        addImagesToArticle(article, articleFromDb);
        return singleResult(articleService.edit(articleFromDb));
    }

    @RequestMapping(value = "/{uuid}", method = RequestMethod.DELETE)
    @ResponseStatus(value = HttpStatus.NO_CONTENT)
    public void delete(@AuthenticationPrincipal final User user, final @PathVariable UUID uuid) {
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

        for (Image image : articleFromDb.getImages()) {
            new File(image.getOriginalPath()).delete();
            new File(image.getThumbnailPath()).delete();
            imageService.delete(image);
        }

        for (Comment comment : articleFromDb.getComments()) {
            commentService.delete(comment);
        }
        articleService.delete(uuid);
    }

    @RequestMapping(value = "/{uuid}/comment", method = RequestMethod.GET)
    public ApiResult getCommentList(
            @AuthenticationPrincipal final User user,
            @PathVariable final UUID uuid,
            final Integer page,
            final Integer limit
    ) {
        Article article = articleService.getByUuid(uuid);
        if (article == null) {
            throw new ArticleNotExistException();
        }

        Page<Comment> comments = commentService.findByArticle(
                ApiUtils.createPageRequest(limit, page, "creationDate:asc"), article);
        if (article.getStatus() == 0) {
            return listResult(comments);
        }

        if (user == null) {
            throw new ArticleNotExistException();
        }
        Account account = accountService.getByEmail(user.getUsername());
        if (article.getAuthor().getId().equals(account.getId())) {
            return listResult(comments);
        }

        if (article.getStatus() == 2 && (account.getAuthority().equals(Role.ROLE_MODERATOR.name())
                || account.getAuthority().equals(Role.ROLE_ADMIN.name()))) {
            return listResult(comments);
        }

        throw new ArticleNotExistException();
    }

    @RequestMapping(value = "/{uuid}/block", method = RequestMethod.GET)
    @ResponseStatus(value = HttpStatus.NO_CONTENT)
    public void block(@AuthenticationPrincipal final User user, @PathVariable final UUID uuid) {
        Account account = accountService.getByEmail(user.getUsername());
        if (!(account.getAuthority().equals(Role.ROLE_ADMIN.name())
                || account.getAuthority().equals(Role.ROLE_MODERATOR.name()))) {
            throw new AccountPermissionException();
        }

        Article article = articleService.getByUuid(uuid);
        if (article == null || article.getStatus() == 1) {
            throw new ArticleNotExistException();
        }

        article.setStatus(2);
        articleService.edit(article);
    }

    @RequestMapping(value = "/{uuid}/unlock", method = RequestMethod.GET)
    @ResponseStatus(value = HttpStatus.NO_CONTENT)
    public void unlock(@AuthenticationPrincipal final User user, @PathVariable final UUID uuid) {
        Account account = accountService.getByEmail(user.getUsername());
        if (!(account.getAuthority().equals(Role.ROLE_ADMIN.name())
                || account.getAuthority().equals(Role.ROLE_MODERATOR.name()))) {
            throw new AccountPermissionException();
        }

        Article article = articleService.getByUuid(uuid);
        if (article == null || article.getStatus() == 1) {
            throw new ArticleNotExistException();
        }


        article.setStatus(0);
        articleService.edit(article);
    }

    private void addImagesToArticle(Article article, Article articleFromDb) {
        if (article.getImages() != null) {
            for (Image image : article.getImages()) {
                Image imageFromDb = imageService.getByUuid(image.getId());
                if (imageFromDb != null && imageFromDb.getArticle() == null) {
                    imageFromDb.setArticle(articleFromDb);
                    imageService.edit(imageFromDb);
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
            Tag tagFromDb = tagService.getByUuid(tag.getId());
            if (tagFromDb != null) {
                article.getTags().add(tagFromDb);
            }
        }
    }
}
