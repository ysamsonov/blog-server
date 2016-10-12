package me.academeg.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.sql.Timestamp;
import java.util.List;

@Entity
@Table(name = "article")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Article {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "author_id", nullable = false)
    private Account author;

    @Column(nullable = false, columnDefinition = "text")
    private String text;

    @Column(nullable = false)
    private Timestamp timestamp;

    @OneToMany(mappedBy = "article")
    private List<ArticlePhoto> photos;

    @OneToMany(mappedBy = "article")
    private List<ArticleVideo> videos;

    @ManyToMany(mappedBy = "articles")
    private List<Tag> tags;

    public Article() {
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public Account getAuthor() {
        return author;
    }

    public void setAuthor(Account author) {
        this.author = author;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public Timestamp getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Timestamp timestamp) {
        this.timestamp = timestamp;
    }

    public List<ArticlePhoto> getPhotos() {
        return photos;
    }

    public void setPhotos(List<ArticlePhoto> photos) {
        this.photos = photos;
    }

    public List<ArticleVideo> getVideos() {
        return videos;
    }

    public void setVideos(List<ArticleVideo> videos) {
        this.videos = videos;
    }

    public List<Tag> getTags() {
        return tags;
    }

    public void setTags(List<Tag> tags) {
        this.tags = tags;
    }
}
