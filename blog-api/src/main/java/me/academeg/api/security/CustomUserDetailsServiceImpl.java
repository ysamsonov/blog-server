package me.academeg.api.security;

import me.academeg.api.entity.Account;
import me.academeg.api.repository.AccountRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.Collection;

/**
 * CustomUserDetailsServiceImpl Service
 *
 * @author Yuriy A. Samsonov <yuriy.samsonov96@gmail.com>
 * @version 1.0
 */
@Component("userDetailsService")
public class CustomUserDetailsServiceImpl implements org.springframework.security.core.userdetails.UserDetailsService {

    @Autowired
    private AccountRepository accountRepository;

    @Override
    @Transactional
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Account accountFromDb = accountRepository.getByEmail(username.toLowerCase());
        if (accountFromDb == null) {
            throw new UsernameNotFoundException("User " + username + " was not found in the database");
        }

        Collection<GrantedAuthority> grantedAuthorities = new ArrayList<>();
        GrantedAuthority grantedAuthority = new SimpleGrantedAuthority(accountFromDb.getAuthority().name());
        grantedAuthorities.add(grantedAuthority);

        return new org.springframework.security.core.userdetails.User(
            accountFromDb.getEmail(),
            accountFromDb.getPassword(),
            grantedAuthorities
        );
    }
}
