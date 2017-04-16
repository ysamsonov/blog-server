package me.academeg.blog.dal.service;

import lombok.extern.slf4j.Slf4j;
import me.academeg.blog.api.exception.BlogEntityExistException;
import me.academeg.blog.api.exception.BlogEntityNotExistException;
import me.academeg.blog.dal.domain.Account;
import me.academeg.blog.dal.repository.AccountRepository;
import me.academeg.blog.security.RoleConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.Collection;
import java.util.List;
import java.util.UUID;

/**
 * @author Yuriy A. Samsonov <yuriy.samsonov96@gmail.com>
 * @version 1.0
 */
@Slf4j
@Service
@Transactional
public class AccountService {

    private final AccountRepository accountRepository;
    private final AvatarService avatarService;
    private final PasswordEncoder passwordEncoder;
    private final TokenStore tokenStore;

    @Autowired
    public AccountService(
        final PasswordEncoder passwordEncoder,
        final AccountRepository accountRepository,
        final AvatarService avatarService,
        final TokenStore tokenStore
    ) {
        this.passwordEncoder = passwordEncoder;
        this.accountRepository = accountRepository;
        this.avatarService = avatarService;
        this.tokenStore = tokenStore;
    }

    public Account create(Account account) {
        checkUniqueness(account);

        Account accountDb = new Account();
        accountDb.setLogin(account.getLogin());
        accountDb.setName(account.getName());
        accountDb.setSurname(account.getSurname());
        accountDb.setEmail(account.getEmail().toLowerCase());
        accountDb.setPassword(passwordEncoder.encode(account.getPassword()));
        accountDb.setEnable(true);
        accountDb.addRole(RoleConstants.USER);
        return accountRepository.save(accountDb);
    }

    public void delete(UUID id) {
        Account account = getById(id);
        if (account == null) {
            throw new BlogEntityNotExistException("Account with id %s not exist", id);
        }

        if (account.getAvatar() != null) {
            avatarService.delete(account.getAvatar().getId());
        }
        account.setArticles(null);
        account.setComments(null);
        tokenStore
            .findTokensByClientIdAndUserName("web_app", account.getEmail())
            .forEach(tokenStore::removeAccessToken);
        accountRepository.delete(account);
    }

    @Transactional(readOnly = true)
    public Account getById(UUID id) {
        return accountRepository.findOne(id);
    }

    @Transactional(readOnly = true)
    public Account getByLogin(String login) {
        return accountRepository.getByLoginIgnoreCase(login);
    }

    @Transactional(readOnly = true)
    public Account getByEmail(String email) {
        return accountRepository.getByEmailIgnoreCase(email);
    }

    @Transactional(readOnly = true)
    public Page<Account> getPage(Pageable pageable) {
        return accountRepository.findAll(pageable);
    }

    public Account update(Account account) {
        checkUniqueness(account);

        Account accountDb = getById(account.getId());
        accountDb.setLogin(account.getLogin());
        accountDb.setName(account.getName());
        accountDb.setSurname(account.getSurname());
        accountDb.setEmail(account.getEmail().toLowerCase());
        if (!StringUtils.isEmpty(account.getPassword())) {
            accountDb.setPassword(passwordEncoder.encode(account.getPassword()));
        }
        return accountRepository.save(accountDb);
    }

    public void block(Collection<UUID> ids) {
        List<Account> accounts = accountRepository.findAll(ids);
        accounts.forEach(acc -> acc.setEnable(false));
        // TODO: 16.04.2017 remove token when block account
        accountRepository.save(accounts);
        accountRepository.flush();
    }

    public void unlock(Collection<UUID> ids) {
        List<Account> accounts = accountRepository.findAll(ids);
        accounts.forEach(acc -> acc.setEnable(true));
        accountRepository.save(accounts);
        accountRepository.flush();
    }

    protected void checkUniqueness(Account account) {
        if (emailExist(account.getEmail(), account.getId())) {
            log.warn("Attempt to create an account with an existing email");
            throw new BlogEntityExistException("Account with email '%s' is already exist", account.getEmail());
        }

        if (loginExist(account.getLogin(), account.getId())) {
            log.warn("Attempt to create an account with an existing login");
            throw new BlogEntityExistException("Account with login '%s' is already exist", account.getLogin());
        }
    }

    private boolean loginExist(final String login, final UUID accountId) {
        Account account = getByLogin(login);
        return account != null && !account.getId().equals(accountId);
    }

    private boolean emailExist(final String email, final UUID accountId) {
        Account account = getByEmail(email);
        return account != null && !account.getId().equals(accountId);
    }
}
