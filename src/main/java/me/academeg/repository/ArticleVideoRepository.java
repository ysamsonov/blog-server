package me.academeg.repository;

import me.academeg.entity.ArticleVideo;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface ArticleVideoRepository extends JpaRepository<ArticleVideo, UUID> {
}
