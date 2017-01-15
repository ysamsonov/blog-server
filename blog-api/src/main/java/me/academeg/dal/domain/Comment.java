package me.academeg.dal.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;
import me.academeg.api.Constants;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Type;
import org.hibernate.validator.constraints.NotEmpty;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.Date;
import java.util.UUID;

/**
 * Comment Entity
 *
 * @author Yuriy A. Samsonov <y.samsonov@erpscan.com>
 * @version 1.0
 */
@Setter
@Getter

@Entity
@Table(name = "comment")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Comment {

    @Id
    @GenericGenerator(name = "uuid-gen", strategy = "uuid2")
    @GeneratedValue(generator = "uuid-gen")
    @Type(type = "uuid-char")
    private UUID id;

    @NotEmpty
    @Column(nullable = false, columnDefinition = "text")
    private String text;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = Constants.DATE_FORMAT)
    @Temporal(TemporalType.TIMESTAMP)
    @Column(nullable = false)
    private Date creationDate;

    @ManyToOne(fetch = FetchType.EAGER)
    private Account author;

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @NotNull
    @ManyToOne
    private Article article;

    public Comment() {
    }

    public Comment(UUID uuid) {
        this.id = uuid;
    }
}
