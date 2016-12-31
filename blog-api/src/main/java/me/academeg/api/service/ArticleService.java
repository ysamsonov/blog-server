package me.academeg.api.service;

import me.academeg.api.entity.Article;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

/**
 * ArticleService Service
 *
 * @author Yuriy A. Samsonov <y.samsonov@erpscan.com>
 * @version 1.0
 */
public interface ArticleService {

    Article create(Article article);

    void delete(UUID id);

    Article getById(UUID id);

    Page<Article> getPage(Pageable pageable);

    Article update(Article article);

    Article lock(Article article);

    Article unlock(Article article);
}
