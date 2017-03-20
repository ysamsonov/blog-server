package me.academeg.blog.security;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

/**
 * @author Yuriy A. Samsonov <y.samsonov@erpscan.com>
 * @date 26.02.2017
 */
@SuppressWarnings("WeakerAccess")
@Getter
@Slf4j
public class UserDetailsImpl implements UserDetails {

    private final UUID id;
    private final String username;
    private final Set<GrantedAuthority> authorities = new HashSet<>();
    private final boolean accountNonExpired;
    private final boolean accountNonLocked;
    private final boolean credentialsNonExpired;
    private final boolean enabled;
    @Setter
    private String password;

    public UserDetailsImpl(
        final UUID id,
        final String username,
        final String password,
        final Collection<? extends GrantedAuthority> authorities
    ) {
        this(
            id,
            username,
            password,
            true,
            true,
            true,
            true,
            authorities
        );
    }

    public UserDetailsImpl(
        final UUID id,
        final String username,
        final String password,
        final boolean enabled,
        final boolean accountNonExpired,
        final boolean credentialsNonExpired,
        final boolean accountNonLocked,
        final Collection<? extends GrantedAuthority> authorities
    ) {
        if (id == null || username == null || "".equals(username) || password == null) {
            log.warn("Try create 'UserDetailsImpl' with wrong credentials");
            throw new IllegalArgumentException("Cannot pass null or empty values to constructor");
        }

        this.id = id;
        this.username = username;
        this.password = password;
        this.enabled = enabled;
        this.accountNonExpired = accountNonExpired;
        this.credentialsNonExpired = credentialsNonExpired;
        this.accountNonLocked = accountNonLocked;
        this.authorities.addAll(authorities);
    }

    public boolean hasAuthority(String role) {
        return hasAuthority(new SimpleGrantedAuthority(role));
    }

    public boolean hasAuthority(SimpleGrantedAuthority role) {
        return this.authorities.contains(role);
    }
}
