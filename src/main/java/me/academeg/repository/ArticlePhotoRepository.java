package me.academeg.repository;

import me.academeg.entity.ArticlePhoto;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ArticlePhotoRepository extends JpaRepository<ArticlePhoto, Long> {
}
