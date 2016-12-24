package me.academeg.api.service;

import me.academeg.api.entity.Account;
import me.academeg.api.entity.Avatar;

import java.util.UUID;

/**
 * AvatarService Service
 *
 * @author Yuriy A. Samsonov <yuriy.samsonov96@gmail.com>
 * @version 1.0
 */
public interface AvatarService {

    Avatar create(Avatar avatar, Account account);

    Avatar getById(UUID id);

    void delete(UUID id);
}
