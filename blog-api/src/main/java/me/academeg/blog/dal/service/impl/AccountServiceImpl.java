package me.academeg.blog.dal.service.impl;

import me.academeg.blog.api.exception.BlogEntityExistException;
import me.academeg.blog.api.exception.BlogEntityNotExistException;
import me.academeg.blog.dal.domain.Account;
import me.academeg.blog.dal.repository.AccountRepository;
import me.academeg.blog.dal.service.AccountService;
import me.academeg.blog.security.RoleConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.stereotype.Service;

import java.util.UUID;

/**
 * AccountServiceImpl Service
 *
 * @author Yuriy A. Samsonov <yuriy.samsonov96@gmail.com>
 * @version 1.0
 */
@Service
public class AccountServiceImpl implements AccountService {

    private final AccountRepository accountRepository;
    private final AvatarServiceImpl avatarService;
    private final PasswordEncoder passwordEncoder;
    private final TokenStore tokenStore;

    @Autowired
    public AccountServiceImpl(
        final PasswordEncoder passwordEncoder,
        final AccountRepository accountRepository,
        final AvatarServiceImpl avatarService,
        final TokenStore tokenStore
    ) {
        this.passwordEncoder = passwordEncoder;
        this.accountRepository = accountRepository;
        this.avatarService = avatarService;
        this.tokenStore = tokenStore;
    }

    @Override
    public Account create(Account account) {
        if (getByEmail(account.getEmail()) != null) {
            throw new BlogEntityExistException(
                String.format("Account with email %s is already exist", account.getEmail()));
        }
        if (getByLogin(account.getLogin()) != null) {
            throw new BlogEntityExistException(
                String.format("Account with login %s is already exist", account.getLogin()));
        }

        Account accountDb = new Account();
        accountDb.setLogin(account.getLogin());
        accountDb.setName(account.getName());
        accountDb.setSurname(account.getSurname());
        accountDb.setEmail(account.getEmail().toLowerCase());
        accountDb.setPassword(passwordEncoder.encode(account.getPassword()));
        accountDb.addRole(RoleConstants.USER);
        return accountRepository.save(accountDb);
    }

    @Override
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

    @Override
    public Account getById(UUID id) {
        return accountRepository.findOne(id);
    }

    @Override
    public Account getByLogin(String login) {
        return accountRepository.getByLoginIgnoreCase(login);
    }

    @Override
    public Account getByEmail(String email) {
        return accountRepository.getByEmailIgnoreCase(email);
    }

    @Override
    public Page<Account> getPage(Pageable pageable) {
        return accountRepository.findAll(pageable);
    }

    @Override
    public Account update(Account account) {
        Account accountDB = getById(account.getId());
        if (!accountDB.getLogin().equals(account.getLogin()) && getByLogin(account.getLogin()) != null) {
            throw new BlogEntityExistException(
                String.format("Account with login %s is already exist", account.getLogin()));
        }

        accountDB.setSurname(account.getSurname());
        accountDB.setName(account.getName());
        accountDB.setLogin(account.getLogin());
        return accountRepository.save(accountDB);
    }
}
