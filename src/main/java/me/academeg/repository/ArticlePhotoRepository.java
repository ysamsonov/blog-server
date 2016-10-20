package me.academeg.repository;

import me.academeg.entity.ArticlePhoto;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface ArticlePhotoRepository extends JpaRepository<ArticlePhoto, UUID> {
}
