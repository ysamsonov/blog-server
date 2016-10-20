package me.academeg.repository;

import me.academeg.entity.Account;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

/**
 * AccountRepository Repository
 *
 * @author Yuriy A. Samsonov <yuriy.samsonov96@gmail.com>
 * @version 1.0
 */
public interface AccountRepository extends JpaRepository<Account, UUID> {

    Account getByEmail(String email);

    Account getByLogin(String login);
}
