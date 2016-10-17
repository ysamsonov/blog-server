package me.academeg.service;

import me.academeg.entity.Account;
import me.academeg.entity.Avatar;

public interface AvatarService {

    Avatar add(Avatar avatar, Account account);

    void delete(Avatar avatar);
}
