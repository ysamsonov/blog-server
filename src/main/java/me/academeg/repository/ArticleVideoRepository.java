package me.academeg.repository;

import me.academeg.entity.ArticleVideo;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

/**
 * ArticleVideoRepository Repository
 *
 * @author Yuriy A. Samsonov <yuriy.samsonov96@gmail.com>
 * @version 1.0
 */
public interface ArticleVideoRepository extends JpaRepository<ArticleVideo, UUID> {
}
