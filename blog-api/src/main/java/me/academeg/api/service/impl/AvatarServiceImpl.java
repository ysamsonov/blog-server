package me.academeg.api.service.impl;

import me.academeg.api.entity.Account;
import me.academeg.api.entity.Avatar;
import me.academeg.api.repository.AvatarRepository;
import me.academeg.api.service.AvatarService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.UUID;

/**
 * AvatarServiceImpl Service
 *
 * @author Yuriy A. Samsonov <yuriy.samsonov96@gmail.com>
 * @version 1.0
 */
@Service
public class AvatarServiceImpl implements AvatarService {

    private AvatarRepository avatarRepository;

    @Autowired
    public AvatarServiceImpl(AvatarRepository avatarRepository) {
        this.avatarRepository = avatarRepository;
    }

    @Override
    public Avatar create(Avatar avatar, Account account) {
        avatar.setAccount(account);
        if (account.getAvatar() != null) {
            avatarRepository.delete(account.getAvatar());
        }
        return avatarRepository.save(avatar);
    }

    @Override
    public Avatar getById(UUID id) {
        return avatarRepository.findOne(id);
    }

    @Override
    public void delete(UUID id) {
        avatarRepository.delete(id);
    }
}
