package me.academeg.repository;

import me.academeg.entity.Article;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.UUID;

/**
 * ArticleRepository Repository
 *
 * @author Yuriy A. Samsonov <yuriy.samsonov96@gmail.com>
 * @version 1.0
 */
public interface ArticleRepository extends PagingAndSortingRepository<Article, UUID> {

    @Override
    @Query("select a from Article a where status = 0")
    Page<Article> findAll(Pageable pageable);
}
