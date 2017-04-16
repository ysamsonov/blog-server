package me.academeg.blog.dal.service;

import com.querydsl.core.types.Predicate;
import me.academeg.blog.dal.domain.Article;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Collection;
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

    Page<Article> getPage(Predicate predicate, Pageable pageable);

    Article update(Article article);

    void block(Collection<UUID> ids);

    void unlock(Collection<UUID> ids);
}
