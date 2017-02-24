package me.academeg.blog.dal.service.impl;

import com.querydsl.core.types.Predicate;
import me.academeg.blog.dal.domain.Article;
import me.academeg.blog.dal.domain.ArticleStatus;
import me.academeg.blog.dal.domain.Image;
import me.academeg.blog.dal.repository.ArticleRepository;
import me.academeg.blog.dal.service.ArticleService;
import me.academeg.blog.dal.service.ImageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.Date;
import java.util.UUID;

/**
 * ArticleServiceImpl
 *
 * @author Yuriy A. Samsonov <y.samsonov@erpscan.com>
 * @version 1.0
 */
@Service
public class ArticleServiceImpl implements ArticleService {
    private final ArticleRepository articleRepository;
    private final ImageService imageService;

    @Autowired
    public ArticleServiceImpl(
        ArticleRepository articleRepository,
        ImageService imageService
    ) {
        this.articleRepository = articleRepository;
        this.imageService = imageService;
    }

    @Override
    public Article create(Article article) {
        if (!article.getStatus().equals(ArticleStatus.PUBLISHED) && !article.getStatus().equals(ArticleStatus.DRAFT)) {
            article.setStatus(ArticleStatus.PUBLISHED);
        }
        article.setCreationDate(new Date());
        addImagesToArticle(article.getImages(), article);
        return articleRepository.saveAndFlush(article);
    }

    @Override
    public void delete(UUID id) {
        Article article = getById(id);
        article.getImages().forEach(image -> imageService.delete(image.getId()));
        articleRepository.delete(id);
    }

    @Override
    public Article getById(UUID id) {
        return articleRepository.findOne(id);
    }

    @Override
    public Page<Article> getPage(Pageable pageable) {
        return articleRepository.findAll(pageable);
    }

    @Override
    public Page<Article> getPage(Predicate predicate, Pageable pageable) {
        return articleRepository.findAll(predicate, pageable);
    }

    @Override
    public Article update(Article article) {
        Article articleFromDb = getById(article.getId());
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
        articleFromDb.getTags().forEach(articleFromDb::removeTag);
        article.getTags().forEach(tag -> {
            articleFromDb.addTag(tag);
            article.removeTag(tag);
        });

        addImagesToArticle(article.getImages(), articleFromDb);
        return articleRepository.save(articleFromDb);
    }

    @Override
    public Article lock(Article article) {
        article.setStatus(ArticleStatus.LOCKED);
        return articleRepository.save(article);
    }

    @Override
    public Article unlock(Article article) {
        article.setStatus(ArticleStatus.PUBLISHED);
        return articleRepository.save(article);
    }

    private void addImagesToArticle(Collection<Image> images, Article article) {
        if (images == null) {
            return;
        }

        images.forEach(image -> {
            Image imageFromDb = imageService.getById(image.getId());
            article.removeImage(image);
            if (imageFromDb != null && imageFromDb.getArticle() == null) {
                imageFromDb.setArticle(article);
                article.addImage(imageFromDb);
            }
        });
    }
}
