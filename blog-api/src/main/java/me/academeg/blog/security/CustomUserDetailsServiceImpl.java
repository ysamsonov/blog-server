package me.academeg.blog.security;

import lombok.extern.slf4j.Slf4j;
import me.academeg.blog.dal.domain.Account;
import me.academeg.blog.dal.domain.AccountRole;
import me.academeg.blog.dal.repository.AccountRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import javax.transaction.Transactional;
import java.util.Collection;
import java.util.stream.Collectors;

/**
 * CustomUserDetailsServiceImpl Service
 *
 * @author Yuriy A. Samsonov <yuriy.samsonov96@gmail.com>
 * @version 1.0
 */
@Component
@Slf4j
public class CustomUserDetailsServiceImpl implements UserDetailsService {

    private AccountRepository accountRepository;

    @Autowired
    public CustomUserDetailsServiceImpl(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

    @Override
    @Transactional
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Account accountFromDb = accountRepository.getByEmailIgnoreCase(username);
        if (accountFromDb == null) {
            String msg = "User " + username + " was not found";
            log.warn(msg);
            throw new UsernameNotFoundException(msg);
        }

        Collection<GrantedAuthority> grantedAuthorities = accountFromDb
            .getRoles()
            .stream()
            .map(AccountRole::name)
            .map(SimpleGrantedAuthority::new)
            .collect(Collectors.toList());

        return new User(
            accountFromDb.getEmail(),
            accountFromDb.getPassword(),
            grantedAuthorities
        );
    }
}
