package me.academeg.controllers;

import me.academeg.entity.Account;
import me.academeg.entity.Avatar;
import me.academeg.service.AccountService;
import me.academeg.service.AvatarService;
import me.academeg.utils.ImageUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.User;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;

@RestController
@RequestMapping("/api")
@Validated
public class AvatarController {

    private static String AVATAR_PATH = "avatars/";

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


        File avatarsDir = new File(AVATAR_PATH);
        if (!avatarsDir.exists()) {
            avatarsDir.mkdir();
        }

        Avatar avatar = new Avatar();
        String originalImageName = ImageUtils.saveImage(avatarsDir, file);
        avatar.setOriginalPath(AVATAR_PATH + originalImageName);
        String thumbnailImageName = ImageUtils.compressImage(new File(avatarsDir, originalImageName), avatarsDir);
        avatar.setThumbnailPath(AVATAR_PATH + thumbnailImageName);

        Account account = accountService.getByEmail(user.getUsername());
        deleteAvatarFromStorage(account.getAvatar());
        return avatarService.set(avatar, account);
    }

    @RequestMapping(value = "/account/delete-avatar", method = RequestMethod.DELETE)
    public ResponseEntity<String> deleteAvatar(@AuthenticationPrincipal User user) {
        Account account = accountService.getByEmail(user.getUsername());
        if (account.getAvatar() != null) {
            avatarService.delete(account.getAvatar());
            deleteAvatarFromStorage(account.getAvatar());
        }
        return new ResponseEntity<>("Avatar delete successful", HttpStatus.OK);
    }

    @RequestMapping(value = "/avatars/{id}", method = RequestMethod.GET)
    public void getAvatar(@PathVariable long id) {

    }

    private void deleteAvatarFromStorage(Avatar avatar) {
        if (avatar == null) {
            return;
        }
        new File(avatar.getOriginalPath()).delete();
        new File(avatar.getThumbnailPath()).delete();
    }
}
