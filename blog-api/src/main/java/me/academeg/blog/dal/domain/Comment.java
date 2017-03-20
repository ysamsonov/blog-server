package me.academeg.blog.dal.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import me.academeg.blog.api.Constants;
import org.hibernate.validator.constraints.NotEmpty;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.Date;
import java.util.UUID;

import static me.academeg.blog.dal.utils.Relations.setManyToOne;

/**
 * Comment Entity
 *
 * @author Yuriy A. Samsonov <y.samsonov@erpscan.com>
 * @version 1.0
 */
@Setter
@Getter
@Accessors(chain = true)

@Entity
@Table(name = "comment")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Comment extends BaseEntity {

    @NotEmpty
    @Column(nullable = false, columnDefinition = "text")
    private String text;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = Constants.DATE_FORMAT)
    @Temporal(TemporalType.TIMESTAMP)
    @Column(nullable = false)
    private Date creationDate;

    @ManyToOne(fetch = FetchType.LAZY)
    private Account author;

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    private Article article;

    public Comment() {
    }

    public Comment(UUID id) {
        super(id);
    }

    // Article ---------------------------------------------------------------------------------
    public Comment setArticle(Article article) {
        return setManyToOne(
            this,
            article,
            Comment::getArticle,
            article1 -> this.article = article1,
            Article::addComment,
            Article::removeComment
        );
    }
    // -----------------------------------------------------------------------------------------

    // Author ----------------------------------------------------------------------------------
    public Comment setAuthor(Account author) {
        return setManyToOne(
            this,
            author,
            Comment::getAuthor,
            author1 -> this.author = author,
            Account::addComment,
            Account::removeComment
        );
    }
    // -----------------------------------------------------------------------------------------
}
