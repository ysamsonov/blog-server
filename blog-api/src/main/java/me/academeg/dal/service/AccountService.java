package me.academeg.dal.service;

import me.academeg.dal.domain.Account;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

/**
 * AccountService Service
 *
 * @author Yuriy A. Samsonov <yuriy.samsonov96@gmail.com>
 * @version 1.0
 */
public interface AccountService {

    Account create(Account account);

    void delete(UUID id);

    Account getById(UUID id);

    Account getByLogin(String login);

    Account getByEmail(String email);

    Page<Account> getPage(Pageable pageable);

    Account update(Account account);
}
