package me.academeg.service.impl;

import me.academeg.entity.Account;
import me.academeg.entity.Avatar;
import me.academeg.repository.AvatarRepository;
import me.academeg.service.AvatarService;
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
    public Avatar set(Avatar avatar, Account account) {
        avatar.setAccount(account);
        if (account.getAvatar() != null) {
            avatarRepository.delete(account.getAvatar());
        }
        return avatarRepository.saveAndFlush(avatar);
    }

    @Override
    public void delete(Avatar avatar) {
        avatarRepository.delete(avatar);
    }

    @Override
    public void delete(UUID id) {
        avatarRepository.delete(id);
    }
}
