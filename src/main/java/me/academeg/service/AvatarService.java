package me.academeg.service;

import me.academeg.entity.Account;
import me.academeg.entity.Avatar;

import java.util.UUID;

public interface AvatarService {

    Avatar set(Avatar avatar, Account account);

    void delete(Avatar avatar);

    void delete(UUID id);
}
