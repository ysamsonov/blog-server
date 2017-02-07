package me.academeg.blog.dal.service.impl;

import me.academeg.blog.dal.domain.Account;
import me.academeg.blog.dal.domain.Avatar;
import me.academeg.blog.dal.repository.AvatarRepository;
import me.academeg.blog.dal.service.AvatarService;
import me.academeg.blog.dal.utils.ImageUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.util.UUID;

/**
 * AvatarServiceImpl Service
 *
 * @author Yuriy A. Samsonov <yuriy.samsonov96@gmail.com>
 * @version 1.0
 */
@Service
public class AvatarServiceImpl implements AvatarService {

    @Value("${me.academeg.blog.avatars.path:avatar/}")
    private String avatarPath;

    private AvatarRepository avatarRepository;

    @Autowired
    public AvatarServiceImpl(AvatarRepository avatarRepository) {
        this.avatarRepository = avatarRepository;
    }

    @Override
    public Avatar create(MultipartFile file, Account account) {
        Avatar avatar = new Avatar();
        avatar.setOriginalPath(ImageUtils.saveImage(avatarPath, file));
        avatar.setThumbnailPath(ImageUtils.compressImage(avatar.getOriginalPath(), avatarPath));
        avatar.setAccount(account);

        if (account.getAvatar() != null) {
            ImageUtils.deleteImages(
                avatarPath,
                account.getAvatar().getOriginalPath(),
                account.getAvatar().getThumbnailPath()
            );
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
        Avatar avatar = avatarRepository.findOne(id);
        ImageUtils.deleteImages(
            avatarPath,
            avatar.getOriginalPath(),
            avatar.getThumbnailPath()
        );
        avatar.getAccount().setAvatar(null);
        avatar.setAccount(null);
        avatarRepository.delete(id);
    }

    @Override
    public byte[] getFile(String name) {
        return ImageUtils.toByteArray(new File(avatarPath + name));
    }
}
