package me.academeg.blog.dal.service.impl;

import me.academeg.blog.dal.domain.AccountRole;
import me.academeg.blog.dal.service.AccountService;
import me.academeg.blog.dal.domain.Account;
import me.academeg.blog.api.exception.EntityExistException;
import me.academeg.blog.dal.repository.AccountRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
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
    private PasswordEncoder passwordEncoder;
    private AccountRepository accountRepository;
    private AvatarServiceImpl avatarService;


    @Autowired
    public AccountServiceImpl(
        PasswordEncoder passwordEncoder,
        AccountRepository accountRepository,
        AvatarServiceImpl avatarService
    ) {
        this.passwordEncoder = passwordEncoder;
        this.accountRepository = accountRepository;
        this.avatarService = avatarService;
    }

    @Override
    public Account create(Account account) {
        if (getByEmail(account.getEmail()) != null) {
            throw new EntityExistException(String.format("Account with email %s is already exist", account.getEmail()));
        }
        if (getByLogin(account.getLogin()) != null) {
            throw new EntityExistException(String.format("Account with login %s is already exist", account.getLogin()));
        }

        Account accountDb = new Account();
        accountDb.setLogin(account.getLogin());
        accountDb.setName(account.getName());
        accountDb.setSurname(account.getSurname());
        accountDb.setEmail(account.getEmail().toLowerCase());
        accountDb.setPassword(passwordEncoder.encode(account.getPassword()));
        accountDb.setAuthority(AccountRole.USER);
        return accountRepository.save(accountDb);
    }

    @Override
    public void delete(UUID id) {
        Account account = getById(id);
        if (account.getAvatar() != null) {
            avatarService.delete(account.getAvatar().getId());
        }
        account.getArticles().forEach(article -> article.setAuthor(null));
        account.setArticles(null);
        account.getComments().forEach(comment -> comment.setAuthor(null));
        account.setComments(null);
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
            throw new EntityExistException(String.format("Account with login %s is already exist", account.getLogin()));
        }

        accountDB.setSurname(account.getSurname());
        accountDB.setName(account.getName());
        accountDB.setLogin(account.getLogin());
        return accountRepository.save(accountDB);
    }
}
