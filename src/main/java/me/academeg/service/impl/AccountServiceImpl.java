package me.academeg.service.impl;

import me.academeg.entity.Account;
import me.academeg.repository.AccountRepository;
import me.academeg.service.AccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

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
    public List<Account> getAll() {
        return accountRepository.findAll();
    }

    @Override
    public Account edit(Account account) {
        return accountRepository.saveAndFlush(account);
    }
}
