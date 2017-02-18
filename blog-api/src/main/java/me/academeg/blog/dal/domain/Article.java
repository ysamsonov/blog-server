package me.academeg.blog.dal.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.Setter;
import me.academeg.blog.api.Constants;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;
import org.hibernate.validator.constraints.NotBlank;

import javax.persistence.*;
import javax.validation.constraints.Size;
import java.util.*;

/**
 * Article Entity
 *
 * @author Yuriy A. Samsonov <yuriy.samsonov96@gmail.com>
 * @version 1.0
 */
@Setter
@Getter

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

    public int getCommentsCount() {
        return this.comments.size();
    }
}
