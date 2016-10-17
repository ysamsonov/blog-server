package me.academeg.controllers;

import me.academeg.entity.Account;
import me.academeg.entity.Avatar;
import me.academeg.service.AccountService;
import me.academeg.service.AvatarService;
import me.academeg.utils.ImageUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.User;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;

@RestController
@RequestMapping("/api")
@Validated
public class AvatarController {

    private AvatarService avatarService;
    private AccountService accountService;

    @Autowired
    public AvatarController(AvatarService avatarService, AccountService accountService) {
        this.avatarService = avatarService;
        this.accountService = accountService;
    }

    @RequestMapping(value = "/account/set-avatar", method = RequestMethod.POST)
    public Avatar setAvatar(@AuthenticationPrincipal User user, @RequestParam("file") MultipartFile file) {
        if (!file.getContentType().startsWith("image/")) {
            throw new IllegalArgumentException("You can upload only images");
        }

        if (file.isEmpty()) {
            throw new IllegalArgumentException("File cannot be empty");
        }

        File dir = new File("avatars");
        if (!dir.exists()) {
            dir.mkdir();
        }

        Avatar avatar = new Avatar();
        String originalImageName = ImageUtils.saveImage(dir, file);
        avatar.setOriginalPath("avatar/" + originalImageName);
        String thumbnailImageName = ImageUtils.compressImage(new File(dir, originalImageName), dir);
        avatar.setThumbnailPath("avatar/" + thumbnailImageName);

        Account account = accountService.getByEmail(user.getUsername());
        return avatarService.add(avatar, account);
    }

    @RequestMapping(value = "/account/delete-avatar", method = RequestMethod.DELETE)
    public void deleteAvatar() {

    }
}
