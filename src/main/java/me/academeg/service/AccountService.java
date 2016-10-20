package me.academeg.service;

import me.academeg.entity.Account;

import java.util.List;
import java.util.UUID;

public interface AccountService {

    Account add(Account account);

    void delete(Account account);

    Account getById(UUID id);

    Account getByLogin(String login);

    Account getByEmail(String email);

    List<Account> getAll();

    Account edit(Account account);
}
