package me.academeg.service.impl;

import me.academeg.entity.Article;
import me.academeg.repository.ArticleRepository;
import me.academeg.service.ArticleService;
import org.springframework.beans.factory.annotation.Autowired;
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
    public Article add(Article article) {
        return articleRepository.save(article);
    }

    @Override
    public void delete(UUID uuid) {
        articleRepository.delete(uuid);
    }

    @Override
    public Article getByUuid(UUID uuid) {
        return articleRepository.findOne(uuid);
    }

    @Override
    public Iterable<Article> getAll(Pageable pageable) {
        return articleRepository.findAll(pageable);
    }

    @Override
    public Article edit(Article article) {
        return articleRepository.save(article);
    }
}
