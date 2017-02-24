package me.academeg.blog.dal.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import me.academeg.blog.api.Constants;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;
import org.hibernate.validator.constraints.NotBlank;

import javax.persistence.*;
import javax.validation.constraints.Size;
import java.util.*;

import static me.academeg.blog.dal.utils.Relations.*;

/**
 * Article Entity
 *
 * @author Yuriy A. Samsonov <yuriy.samsonov96@gmail.com>
 * @version 1.0
 */
@Setter
@Getter
@Accessors(chain = true)

@Entity
@Table(name = "article")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Article extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "author_id")
    private Account author;

    @Column(nullable = false)
    @NotBlank
    @Size(min = 4, max = 255)
    private String title;

    @Column(nullable = false, columnDefinition = "text")
    @NotBlank
    private String text;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private ArticleStatus status;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(nullable = false)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = Constants.DATE_FORMAT)
    private Date creationDate;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "article", orphanRemoval = true, fetch = FetchType.LAZY)
    private Set<Image> images = new HashSet<>();

    @JsonIgnore
    @LazyCollection(LazyCollectionOption.EXTRA)
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "article", orphanRemoval = true, fetch = FetchType.LAZY)
    private List<Comment> comments = new ArrayList<>();

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
        name = "article_tag",
        joinColumns = @JoinColumn(name = "article_id", referencedColumnName = "id"),
        inverseJoinColumns = @JoinColumn(name = "tag_id", referencedColumnName = "id")
    )
    private Set<Tag> tags = new HashSet<>();

    public Article() {
    }

    public Article(UUID id) {
        super(id);
    }


    // Account ---------------------------------------------------------------------------------
    public Article setAuthor(Account account) {
        return setManyToOne(
            this,
            account,
            Article::getAuthor,
            account1 -> this.author = account1,
            Account::addArticle,
            Account::removeArticle
        );
    }
    // -----------------------------------------------------------------------------------------

    // Images ----------------------------------------------------------------------------------
    public Article addImage(Image image) {
        return addOneToMany(
            this,
            this.images,
            image,
            Image::getArticle,
            Image::setArticle
        );
    }

    public Article removeImage(Image image) {
        return removeOneToMany(
            this,
            this.images,
            image,
            Image::setArticle
        );
    }

    public Collection<Image> getImages() {
        return getOneToMany(this.images);
    }

    public Article setImages(Collection<Image> images) {
        return setOneToMany(
            this,
            this.images,
            images,
            Image::getArticle,
            Image::setArticle
        );
    }
    // -----------------------------------------------------------------------------------------

    // Comments --------------------------------------------------------------------------------
    public Article addComment(Comment comment) {
        return addOneToMany(
            this,
            this.comments,
            comment,
            Comment::getArticle,
            Comment::setArticle
        );
    }

    public Article removeComment(Comment comment) {
        return removeOneToMany(
            this,
            this.comments,
            comment,
            Comment::setArticle
        );
    }

    public Collection<Comment> getComments() {
        return getOneToMany(this.comments);
    }

    public Article setComments(Collection<Comment> comments) {
        return setOneToMany(
            this,
            this.comments,
            comments,
            Comment::getArticle,
            Comment::setArticle
        );
    }

    public int getCommentsCount() {
        return this.comments.size();
    }
    // -----------------------------------------------------------------------------------------

    // Tag -------------------------------------------------------------------------------------
    public Article addTag(Tag tag) {
        return addManyToMany(
            this,
            tag,
            Article::hasTag,
            tags::add,
            Tag::addArticle
        );
    }

    public Article removeTag(Tag tag) {
        return removeManyToMany(
            this,
            tag,
            Article::hasTag,
            tags::remove,
            Tag::removeArticle
        );
    }

    public boolean hasTag(Tag tag) {
        return this.tags.contains(tag);
    }

//    public Collection<Tag> getTags() {
//        return getOneToMany(this.tags);
//    }
//
//    public Article setTags(Collection<Tag> tags) {
//        setManyToMany(
//            tags,
//            this.tags,
//            this::removeTag,
//            this::addTag
//        );
//        return this;
//    }
    // -----------------------------------------------------------------------------------------
}
