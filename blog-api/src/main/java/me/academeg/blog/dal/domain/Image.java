package me.academeg.blog.dal.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import javax.persistence.*;
import java.util.UUID;

import static me.academeg.blog.dal.utils.Relations.setManyToOne;

/**
 * Image Entity
 *
 * @author Yuriy A. Samsonov <yuriy.samsonov96@gmail.com>
 * @version 1.0
 */
@Setter
@Getter
@Accessors(chain = true)

@Entity
@Table(name = "image")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Image extends BaseEntity {

    @Column(nullable = false)
    private String originalPath;

    @Column(nullable = false)
    private String thumbnailPath;

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @ManyToOne(fetch = FetchType.LAZY)
    private Article article;

    public Image() {
    }

    public Image(UUID id) {
        super(id);
    }

    // Article ---------------------------------------------------------------------------------
    public Image setArticle(Article article) {
        return setManyToOne(
            this,
            article,
            Image::getArticle,
            article1 -> this.article = article1,
            Article::addImage,
            Article::removeImage
        );
    }
    // -----------------------------------------------------------------------------------------
}
