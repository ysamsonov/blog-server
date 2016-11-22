package me.academeg.api.rest;

import me.academeg.entity.*;
import me.academeg.exceptions.AccountPermissionException;
import me.academeg.exceptions.ArticleNotExistException;
import me.academeg.exceptions.EmptyFieldException;
import me.academeg.security.Role;
import me.academeg.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
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
    private ImageService imageService;
    private CommentService commentService;
    private TagService tagService;

    @Autowired
    public ArticleController(ArticleService articleService,
                             AccountService accountService,
                             ImageService imageService,
                             CommentService commentService,
                             TagService tagService) {

        this.articleService = articleService;
        this.accountService = accountService;
        this.imageService = imageService;
        this.commentService = commentService;
        this.tagService = tagService;
    }

    @RequestMapping(value = "/{uuid}", method = RequestMethod.GET)
    public Article getByUuid(@PathVariable UUID uuid) {
        Article articleFromDb = articleService.getByUuid(uuid);
        if (articleFromDb == null) {
            throw new ArticleNotExistException();
        }
        return articleFromDb;
    }

    @RequestMapping(value = "", method = RequestMethod.GET)
    public Page<Article> getPage(@RequestParam(defaultValue = "0") int page,
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

        Article saveArticle = new Article();
        saveArticle.setAuthor(accountService.getByEmail(user.getUsername()));
        saveArticle.setTitle(article.getTitle());
        saveArticle.setText(article.getText());
        saveArticle.setCreationDate(Calendar.getInstance());
        saveArticle.setTags(new HashSet<>());
        addTagsToArticle(article.getTags(), saveArticle);

        Article articleFromDb = articleService.add(saveArticle);
        articleFromDb.setImages(new HashSet<>());
        addImageToArticle(article, articleFromDb);
        return articleFromDb;
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
        articleFromDb.getTags().clear();
        addTagsToArticle(article.getTags(), articleFromDb);
        addImageToArticle(article, articleFromDb);
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

    private void addImageToArticle(Article article, Article articleFromDb) {
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
