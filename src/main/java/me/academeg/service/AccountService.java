package me.academeg.service;

import me.academeg.entity.Account;

public interface AccountService {

    Account add(Account account);

    void delete(Account account);

    Account getById(long id);

    Account getByLogin(String login);

    Account getByEmail(String email);

    Account edit(Account account);
}
