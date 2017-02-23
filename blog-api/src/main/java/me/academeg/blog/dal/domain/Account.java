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

    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "account")
    private Avatar avatar;

    @JsonIgnore
    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "author")
    private List<Article> articles = new ArrayList<>();

    @JsonIgnore
    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "author")
    private List<Comment> comments = new ArrayList<>();

    @Enumerated(EnumType.STRING)
    @ElementCollection(fetch = FetchType.LAZY)
    private Set<AccountRole> roles = new HashSet<>();

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

    public boolean hasRole(AccountRole role) {
        return this.roles.contains(role);
    }
}
