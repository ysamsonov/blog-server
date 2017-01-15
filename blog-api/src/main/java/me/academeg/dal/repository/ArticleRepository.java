package me.academeg.dal.repository;

import me.academeg.dal.domain.Article;

import java.util.UUID;

/**
 * ArticleRepository Repository
 *
 * @author Yuriy A. Samsonov <yuriy.samsonov96@gmail.com>
 * @version 1.0
 */
public interface ArticleRepository extends BaseRepository<Article, UUID> {
}
