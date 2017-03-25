package me.academeg.blog.dal.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.hibernate.validator.constraints.Email;
import org.hibernate.validator.constraints.NotBlank;

import javax.persistence.*;
import javax.validation.constraints.Size;
import java.util.*;

import static me.academeg.blog.dal.utils.Relations.*;

/**
 * Account Entity
 *
 * @author Yuriy A. Samsonov <yuriy.samsonov96@gmail.com>
 * @version 1.0
 */
@Setter
@Getter
@Accessors(chain = true)

@Entity
@Table(name = "account")
@JsonIgnoreProperties(value = {"hibernateLazyInitializer", "handler"}, ignoreUnknown = true)
public class Account extends BaseEntity {

    @NotBlank
    @Column(nullable = false, unique = true)
    private String login;

    @Column
    private String name;

    @Column
    private String surname;

    @Email
    @Column(nullable = false, unique = true)
    private String email;

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @Column(nullable = false)
    @Size(min = 4, max = 255)
    @NotBlank
    private String password;

    private boolean enable;

    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "account")
    private Avatar avatar;

    @JsonIgnore
    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "author")
    private List<Article> articles = new ArrayList<>();

    @JsonIgnore
    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "author")
    private List<Comment> comments = new ArrayList<>();

    @ElementCollection(fetch = FetchType.LAZY)
    private Set<String> roles = new HashSet<>();

    public Account() {
    }

    public Account(UUID id) {
        super(id);
    }

    // Avatar ----------------------------------------------------------------------------------
    public Account setAvatar(Avatar avatar) {
        if (this.avatar == avatar) {
            return this;
        }

        if (this.avatar != null) {
            Avatar tmpAvatar = this.avatar;
            this.avatar = null;
            tmpAvatar.setAccount(null);
        }

        if (avatar != null) {
            if (this.avatar == avatar) {
                return this;
            }
            this.avatar = avatar;
            avatar.setAccount(this);
        }

        return this;
    }
    // -----------------------------------------------------------------------------------------

    // Articles --------------------------------------------------------------------------------
    public Account addArticle(Article article) {
        return addOneToMany(
            this,
            this.articles,
            article,
            Article::getAuthor,
            Article::setAuthor
        );
    }

    public Account removeArticle(Article article) {
        return removeOneToMany(
            this,
            this.articles,
            article,
            Article::setAuthor
        );
    }

    public Collection<Article> getArticles() {
        return getOneToMany(this.articles);
    }

    public Account setArticles(Collection<Article> articles) {
        return setOneToMany(
            this,
            this.articles,
            articles,
            Article::getAuthor,
            Article::setAuthor
        );
    }
    // -----------------------------------------------------------------------------------------

    // Comments --------------------------------------------------------------------------------
    public Account addComment(Comment comment) {
        return addOneToMany(
            this,
            this.comments,
            comment,
            Comment::getAuthor,
            Comment::setAuthor
        );
    }

    public Account removeComment(Comment comment) {
        return removeOneToMany(
            this,
            this.comments,
            comment,
            Comment::setAuthor
        );
    }

    public Collection<Comment> getComments() {
        return getOneToMany(this.comments);
    }

    public Account setComments(Collection<Comment> comments) {
        return setOneToMany(
            this,
            this.comments,
            comments,
            Comment::getAuthor,
            Comment::setAuthor
        );
    }
    // -----------------------------------------------------------------------------------------

    // Roles -----------------------------------------------------------------------------------
    public Account addRole(String role) {
        this.roles.add(role);
        return this;
    }

    public Account removeRole(String role) {
        this.roles.remove(role);
        return this;
    }

    public boolean hasRole(String role) {
        return this.roles.contains(role);
    }

    public Collection<String> getRoles() {
        return getOneToMany(this.roles);
    }

    public Account setRoles(Collection<String> roles) {
        this.roles.clear();
        this.roles.addAll(roles);
        return this;
    }
    // -----------------------------------------------------------------------------------------
}
