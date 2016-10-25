package me.academeg.service;

import me.academeg.entity.Account;

import java.util.UUID;

/**
 * AccountService Service
 *
 * @author Yuriy A. Samsonov <yuriy.samsonov96@gmail.com>
 * @version 1.0
 */
public interface AccountService {

    Account add(Account account);

    void delete(Account account);

    Account getById(UUID id);

    Account getByLogin(String login);

    Account getByEmail(String email);

    Iterable<Account> getAll();

    Account edit(Account account);
}
