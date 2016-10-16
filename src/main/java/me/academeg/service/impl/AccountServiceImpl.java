package me.academeg.service.impl;

import me.academeg.entity.Account;
import me.academeg.repository.AccountRepository;
import me.academeg.service.AccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AccountServiceImpl implements AccountService {

    @Autowired
    private AccountRepository accountRepository;


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
        return accountRepository.getOne(id);
    }

    @Override
    public Account getByLogin(String login) {

        return null;
    }

    @Override
    public Account getByEmail(String email) {
        return null;
    }

    @Override
    public Account edit(Account account) {
        return accountRepository.saveAndFlush(account);
    }
}
