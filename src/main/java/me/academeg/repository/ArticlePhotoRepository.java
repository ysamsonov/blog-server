package me.academeg.repository;

import me.academeg.entity.ArticlePhoto;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

/**
 * ArticlePhotoRepository Repository
 *
 * @author Yuriy A. Samsonov <yuriy.samsonov96@gmail.com>
 * @version 1.0
 */
public interface ArticlePhotoRepository extends JpaRepository<ArticlePhoto, UUID> {
}
