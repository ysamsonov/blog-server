package me.academeg.blog.dal.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.hibernate.validator.constraints.NotBlank;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static me.academeg.blog.dal.utils.Relations.addManyToMany;
import static me.academeg.blog.dal.utils.Relations.removeManyToMany;

/**
 * Tag Entity
 *
 * @author Yuriy A. Samsonov <yuriy.samsonov96@gmail.com>
 * @version 1.0
 */
@Setter
@Getter
@Accessors(chain = true)

@Entity
@Table(name = "tag")
@JsonIgnoreProperties(value = {"hibernateLazyInitializer", "handler"}, ignoreUnknown = true)
public class Tag extends BaseEntity {

    @NotBlank
    @Column(nullable = false, unique = true)
    private String value;

    @JsonIgnore
    @ManyToMany(cascade = CascadeType.ALL, mappedBy = "tags", fetch = FetchType.LAZY)
    private List<Article> articles = new ArrayList<>();

    public Tag() {
    }

    public Tag(UUID id) {
        super(id);
    }

    // Articles --------------------------------------------------------------------------------
    public Tag addArticle(Article article) {
        return addManyToMany(
            this,
            article,
            Tag::hasArticle,
            this.articles::add,
            Article::addTag
        );
    }

    public Tag removeArticle(Article article) {
        return removeManyToMany(
            this,
            article,
            Tag::hasArticle,
            this.articles::remove,
            Article::removeTag
        );
    }

    public boolean hasArticle(Article article) {
        return this.articles.contains(article);
    }

//    public Collection<Article> getArticles() {
//        return getOneToMany(this.articles);
//    }
//
//    public Tag setArticles(Collection<Article> articles) {
//        setManyToMany(
//            articles,
//            this.articles,
//            this::removeArticle,
//            this::addArticle
//        );
//        return this;
//    }
    // -----------------------------------------------------------------------------------------
}
