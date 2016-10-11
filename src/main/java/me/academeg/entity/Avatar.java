package me.academeg.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;

@Entity
@Table(name = "avatar")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Avatar {

    @Id
    @GeneratedValue(generator = "system-uuid")
    @GenericGenerator(name = "system-uuid", strategy = "uuid")
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
