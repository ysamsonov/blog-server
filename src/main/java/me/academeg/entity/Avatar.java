package me.academeg.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import javax.persistence.*;

@Entity
@Table(name = "avatar")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Avatar {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    @Column(nullable = false)
    private String originalPath;

    @Column(nullable = false)
    private String thumbnailPath;

    @OneToOne(mappedBy = "avatar")
    private Account account;

    public Avatar() {
    }

    public long getId() {
        return id;
    }

    public String getOriginalPath() {
        return originalPath;
    }

    public String getThumbnailPath() {
        return thumbnailPath;
    }
}
