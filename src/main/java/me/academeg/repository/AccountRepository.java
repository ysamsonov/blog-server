package me.academeg.repository;

import me.academeg.entity.Account;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface AccountRepository extends JpaRepository<Account, UUID> {

    Account getByEmail(String email);

    Account getByLogin(String login);
}
