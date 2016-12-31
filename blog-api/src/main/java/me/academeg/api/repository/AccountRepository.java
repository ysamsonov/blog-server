package me.academeg.api.repository;

import me.academeg.api.entity.Account;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.UUID;

/**
 * AccountRepository Repository
 *
 * @author Yuriy A. Samsonov <yuriy.samsonov96@gmail.com>
 * @version 1.0
 */
public interface AccountRepository extends PagingAndSortingRepository<Account, UUID> {

    Account getByEmailIgnoreCase(String email);

    Account getByLoginIgnoreCase(String login);
}
