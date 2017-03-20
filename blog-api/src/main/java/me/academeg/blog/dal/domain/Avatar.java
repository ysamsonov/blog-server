package me.academeg.blog.dal.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import javax.persistence.*;
import java.util.UUID;

/**
 * Avatar Entity
 *
 * @author Yuriy A. Samsonov <yuriy.samsonov96@gmail.com>
 * @version 1.0
 */
@Setter
@Getter
@Accessors(chain = true)

@Entity
@Table(name = "avatar")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Avatar extends BaseEntity {

    @Column(nullable = false)
    private String originalPath;

    @Column(nullable = false)
    private String thumbnailPath;

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @OneToOne(fetch = FetchType.LAZY)
    private Account account;

    public Avatar() {
    }

    public Avatar(UUID id) {
        super(id);
    }

    // Account ---------------------------------------------------------------------------------
    public Avatar setAccount(Account account) {
        if (this.account == account) {
            return this;
        }

        if (this.account != null) {
            Account tmpAccount = this.account;
            this.account = null;
            tmpAccount.setAvatar(null);
        }

        if (account != null) {
            if (this.account == account) {
                return this;
            }
            this.account = account;
            account.setAvatar(this);
        }
        return this;
    }
    // -----------------------------------------------------------------------------------------
}
