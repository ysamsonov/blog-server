package me.academeg.blog.dal.service;

import com.querydsl.core.types.Predicate;
import me.academeg.blog.api.utils.WhiteListFactory;
import me.academeg.blog.dal.domain.Article;
import me.academeg.blog.dal.domain.ArticleStatus;
import me.academeg.blog.dal.domain.Image;
import me.academeg.blog.dal.repository.ArticleRepository;
import org.jsoup.Jsoup;
import org.jsoup.safety.Whitelist;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @author Yuriy A. Samsonov <y.samsonov@erpscan.com>
 * @version 1.0
 */
@Service
@Transactional
public class ArticleService {

    private final ArticleRepository articleRepository;
    private final ImageService imageService;
    private final Whitelist tagsWhiteList = WhiteListFactory.getDefaultWithVideos();

    @Autowired
    public ArticleService(
        final ArticleRepository articleRepository,
        final ImageService imageService
    ) {
        this.articleRepository = articleRepository;
        this.imageService = imageService;
    }

    public Article create(Article article) {
        if (!article.getStatus().equals(ArticleStatus.PUBLISHED) && !article.getStatus().equals(ArticleStatus.DRAFT)) {
            article.setStatus(ArticleStatus.PUBLISHED);
        }
        article.setText(Jsoup.clean(article.getText(), tagsWhiteList));
        article.setCreationDate(new Date());
        addImagesToArticle(article.getImages(), article);
        return articleRepository.saveAndFlush(article);
    }

    public void delete(UUID id) {
        Article article = getById(id);
        article.getImages().forEach(image -> imageService.delete(image.getId()));
        articleRepository.delete(id);
    }

    @Transactional(readOnly = true)
    public Article getById(UUID id) {
        return articleRepository.findOne(id);
    }

    @Transactional(readOnly = true)
    public Page<Article> getPage(Pageable pageable) {
        return articleRepository.findAll(pageable);
    }

    @Transactional(readOnly = true)
    public Page<Article> getPage(Predicate predicate, Pageable pageable) {
        return articleRepository.findAll(predicate, pageable);
    }

    public Article update(Article article) {
        Article articleFromDb = getById(article.getId());
        articleFromDb.setTitle(article.getTitle());
        articleFromDb.setText(Jsoup.clean(article.getText(), tagsWhiteList));
        if (!articleFromDb.getStatus().equals(ArticleStatus.LOCKED)) {
            if (article.getStatus() != null) {
                if (article.getStatus().equals(ArticleStatus.LOCKED)) {
                    article.setStatus(ArticleStatus.DRAFT);
                }
                articleFromDb.setStatus(article.getStatus());
            }
        }
        articleFromDb.getTags().forEach(articleFromDb::removeTag);
        new ArrayList<>(article.getTags()).forEach(tag -> {
            articleFromDb.addTag(tag);
            article.removeTag(tag);
        });

        addImagesToArticle(article.getImages(), articleFromDb);
        return articleRepository.saveAndFlush(articleFromDb);
    }

    public void block(Collection<UUID> ids) {
        List<Article> articles = articleRepository
            .findAll(ids)
            .stream()
            .unordered()
            .filter(a -> !a.getStatus().equals(ArticleStatus.DRAFT))
            .map(a -> a.setStatus(ArticleStatus.LOCKED))
            .collect(Collectors.toList());

        articleRepository.save(articles);
        articleRepository.flush();
    }

    public void unlock(Collection<UUID> ids) {
        List<Article> articles = articleRepository
            .findAll(ids)
            .stream()
            .unordered()
            .filter(a -> !a.getStatus().equals(ArticleStatus.DRAFT))
            .map(a -> a.setStatus(ArticleStatus.PUBLISHED))
            .collect(Collectors.toList());

        articleRepository.save(articles);
        articleRepository.flush();
    }

    private void addImagesToArticle(Collection<Image> images, Article article) {
        if (images == null) {
            return;
        }

        new ArrayList<>(images).forEach(image -> {
            Image imageFromDb = imageService.getById(image.getId());
            article.removeImage(image);
            if (imageFromDb != null && imageFromDb.getArticle() == null) {
                imageFromDb.setArticle(article);
                article.addImage(imageFromDb);
            }
        });
    }
}
