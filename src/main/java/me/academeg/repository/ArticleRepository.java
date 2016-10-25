package me.academeg.repository;

import me.academeg.entity.Article;
import org.springframework.data.repository.CrudRepository;

import java.util.UUID;

/**
 * ArticleRepository Repository
 *
 * @author Yuriy A. Samsonov <yuriy.samsonov96@gmail.com>
 * @version 1.0
 */
public interface ArticleRepository extends CrudRepository<Article, UUID> {
}
