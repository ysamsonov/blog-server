package me.academeg.service;

import me.academeg.entity.Account;
import me.academeg.entity.Avatar;

import java.util.UUID;

/**
 * AvatarService Service
 *
 * @author Yuriy A. Samsonov <yuriy.samsonov96@gmail.com>
 * @version 1.0
 */
public interface AvatarService {

    Avatar set(Avatar avatar, Account account);

    void delete(Avatar avatar);

    void delete(UUID id);
}
