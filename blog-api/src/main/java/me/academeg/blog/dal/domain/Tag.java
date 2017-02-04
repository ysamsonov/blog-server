package me.academeg.blog.dal.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.NotBlank;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Tag Entity
 *
 * @author Yuriy A. Samsonov <yuriy.samsonov96@gmail.com>
 * @version 1.0
 */
@Setter
@Getter

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
}
