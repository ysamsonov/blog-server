package me.academeg.repository;

import me.academeg.entity.Article;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.UUID;

/**
 * ArticleRepository Repository
 *
 * @author Yuriy A. Samsonov <yuriy.samsonov96@gmail.com>
 * @version 1.0
 */
public interface ArticleRepository extends PagingAndSortingRepository<Article, UUID> {
}
