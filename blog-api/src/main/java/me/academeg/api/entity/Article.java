package me.academeg.api.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Type;
import org.hibernate.validator.constraints.NotBlank;

import javax.persistence.*;
import javax.validation.constraints.Size;
import java.util.Calendar;
import java.util.List;
import java.util.Set;
import java.util.UUID;

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
public class Article {

    @Id
    @GenericGenerator(name = "uuid-gen", strategy = "uuid2")
    @GeneratedValue(generator = "uuid-gen")
    @Type(type = "uuid-char")
    private UUID id;

    @ManyToOne(fetch = FetchType.EAGER)
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
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Calendar creationDate;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "article")
    private Set<Image> images;

    @JsonIgnore
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "article")
    private List<Comment> comments;

    @ManyToMany(cascade = CascadeType.ALL)
    @JoinTable(
        name = "article_tag",
        joinColumns = @JoinColumn(name = "article_id", referencedColumnName = "id"),
        inverseJoinColumns = @JoinColumn(name = "tag_id", referencedColumnName = "id")
    )
    private Set<Tag> tags;

    public Article() {
    }

    public Article(UUID uuid) {
        this.id = uuid;
    }
}
