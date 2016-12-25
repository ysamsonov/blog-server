package me.academeg.api.service.impl;

import me.academeg.api.Constants;
import me.academeg.api.entity.Article;
import me.academeg.api.entity.ArticleStatus;
import me.academeg.api.entity.Image;
import me.academeg.api.repository.ArticleRepository;
import me.academeg.api.service.ArticleService;
import me.academeg.api.service.ImageService;
import me.academeg.api.utils.ImageUtils;
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

    private ArticleRepository articleRepository;
    private ImageService imageService;

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
        Article saveArticle = new Article(UUID.randomUUID());
        saveArticle.setAuthor(article.getAuthor());
        saveArticle.setTitle(article.getTitle());
        saveArticle.setText(article.getText());
        if (!article.getStatus().equals(ArticleStatus.PUBLISHED) || !article.getStatus().equals(ArticleStatus.DRAFT)) {
            article.setStatus(ArticleStatus.PUBLISHED);
        }
        saveArticle.setStatus(article.getStatus());
        saveArticle.setCreationDate(new Date());
        saveArticle.setTags(article.getTags());

        Article articleFromDb = articleRepository.save(saveArticle);
        addImagesToArticle(article.getImages(), articleFromDb);

        return articleFromDb;
    }

    @Override
    public void delete(UUID id) {
        Article article = getById(id);
        article.getImages().forEach(
            image -> ImageUtils.deleteImages(
                Constants.IMAGE_PATH,
                image.getThumbnailPath(),
                image.getOriginalPath())
        );
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
        articleFromDb.getTags().clear();
        articleFromDb.getTags().addAll(article.getTags());
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
            if (imageFromDb != null && imageFromDb.getArticle() == null) {
                imageFromDb.setArticle(article);
                imageService.update(imageFromDb);
                article.getImages().add(imageFromDb);
            }
        });
    }
}
