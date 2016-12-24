package me.academeg.api.service.impl;

import me.academeg.api.entity.Account;
import me.academeg.api.repository.AccountRepository;
import me.academeg.api.service.AccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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

    private AccountRepository accountRepository;

    @Autowired
    public AccountServiceImpl(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

    @Override
    public Account create(Account account) {
        return accountRepository.save(account);
    }

    @Override
    public void delete(Account account) {
        accountRepository.delete(account);
    }

    @Override
    public Account getById(UUID id) {
        return accountRepository.findOne(id);
    }

    @Override
    public Account getByLogin(String login) {
        return accountRepository.getByLogin(login.toLowerCase());
    }

    @Override
    public Account getByEmail(String email) {
        return accountRepository.getByEmail(email.toLowerCase());
    }

    @Override
    public Page<Account> getPage(Pageable pageable) {
        return accountRepository.findAll(pageable);
    }

    @Override
    public Account update(Account account) {
        return accountRepository.save(account);
    }
}
