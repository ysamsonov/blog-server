package me.academeg.service;

import me.academeg.entity.Article;
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

    Article add(Article article);

    void delete(UUID uuid);

    Article getByUuid(UUID uuid);

    Page<Article> getAll(Pageable pageable);

    Article edit(Article article);
}
