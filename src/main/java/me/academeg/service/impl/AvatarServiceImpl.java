package me.academeg.service.impl;

import me.academeg.entity.Account;
import me.academeg.entity.Avatar;
import me.academeg.repository.AvatarRepository;
import me.academeg.service.AccountService;
import me.academeg.service.AvatarService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AvatarServiceImpl implements AvatarService {

    private AvatarRepository avatarRepository;
    private AccountService accountService;

    @Autowired
    public AvatarServiceImpl(AvatarRepository avatarRepository, AccountService accountService) {
        this.avatarRepository = avatarRepository;
        this.accountService = accountService;
    }

    @Override
    public Avatar add(Avatar avatar, Account account) {
        avatar.setAccount(account);
        Avatar avaDb = avatarRepository.saveAndFlush(avatar);
        account.setAvatar(avaDb);
        accountService.edit(account);
        return avaDb;
    }

    @Override
    public void delete(Avatar avatar) {
        avatarRepository.delete(avatar);
    }
}
