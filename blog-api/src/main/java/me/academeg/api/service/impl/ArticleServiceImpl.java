package me.academeg.api.service.impl;

import me.academeg.api.repository.ArticleRepository;
import me.academeg.api.service.ArticleService;
import me.academeg.api.entity.Article;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

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

    @Autowired
    public ArticleServiceImpl(ArticleRepository articleRepository) {
        this.articleRepository = articleRepository;
    }

    @Override
    public Article create(Article article) {
        return articleRepository.save(article);
    }

    @Override
    public void delete(UUID id) {
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
        return articleRepository.save(article);
    }
}
