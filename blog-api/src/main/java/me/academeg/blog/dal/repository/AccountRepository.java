package me.academeg.blog.dal.repository;

import me.academeg.blog.dal.domain.Account;

import java.util.UUID;

/**
 * AccountRepository Repository
 *
 * @author Yuriy A. Samsonov <yuriy.samsonov96@gmail.com>
 * @version 1.0
 */
public interface AccountRepository extends BaseRepository<Account, UUID> {

    Account getByEmailIgnoreCase(String email);

    Account getByLoginIgnoreCase(String login);
}
