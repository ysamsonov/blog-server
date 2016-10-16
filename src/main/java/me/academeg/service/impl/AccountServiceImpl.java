package me.academeg.service.impl;

import me.academeg.entity.Account;
import me.academeg.repository.AccountRepository;
import me.academeg.service.AccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AccountServiceImpl implements AccountService {

    private AccountRepository accountRepository;

    @Autowired
    public AccountServiceImpl(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

    @Override
    public Account add(Account account) {
        return accountRepository.saveAndFlush(account);
    }

    @Override
    public void delete(Account account) {
        accountRepository.delete(account);
    }

    @Override
    public Account getById(long id) {
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
    public Account edit(Account account) {
        return accountRepository.saveAndFlush(account);
    }
}
