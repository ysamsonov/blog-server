package me.academeg.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "tag")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Tag {

    @Id
    @GenericGenerator(name = "uuid-gen", strategy = "uuid2")
    @GeneratedValue(generator = "uuid-gen")
    private UUID id;

    @Column(nullable = false, unique = true)
    private String value;

    @ManyToMany
    @JoinTable(name = "article_tag",
            joinColumns = @JoinColumn(name = "tag_id", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(name = "article_id", referencedColumnName = "id"))
    private List<Article> articles;

    public Tag() {
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public List<Article> getArticles() {
        return articles;
    }
}
