package me.academeg.api.controller;

import me.academeg.api.Constants;
import me.academeg.api.common.ApiResult;
import me.academeg.api.entity.Account;
import me.academeg.api.entity.Avatar;
import me.academeg.api.exception.EntityNotExistException;
import me.academeg.api.exception.entity.FileFormatException;
import me.academeg.api.service.AccountService;
import me.academeg.api.service.AvatarService;
import me.academeg.api.utils.ApiUtils;
import me.academeg.api.utils.ImageUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.User;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.util.UUID;

import static me.academeg.api.utils.ApiUtils.singleResult;

/**
 * AvatarController Controller
 *
 * @author Yuriy A. Samsonov <yuriy.samsonov96@gmail.com>
 * @version 1.0
 */
@RestController
@RequestMapping("/api/avatar")
@Validated
public class AvatarController {

    private final AvatarService avatarService;
    private final AccountService accountService;

    @Autowired
    public AvatarController(
        AvatarService avatarService,
        AccountService accountService
    ) {
        this.avatarService = avatarService;
        this.accountService = accountService;
    }

    @RequestMapping(value = "", method = RequestMethod.POST)
    public ApiResult create(
        @AuthenticationPrincipal final User user,
        @RequestParam final MultipartFile image
    ) {
        if (!image.getContentType().startsWith("image/")) {
            throw new FileFormatException("You can upload only images");
        }

        Avatar avatar = new Avatar();
        String originalImageName = ImageUtils.saveImage(Constants.AVATAR_PATH, image);
        avatar.setOriginalPath(originalImageName);
        String thumbnailImageName = ImageUtils.compressImage(originalImageName, Constants.AVATAR_PATH);
        avatar.setThumbnailPath(thumbnailImageName);

        Account account = accountService.getByEmail(user.getUsername());
        deleteAvatarFromStorage(account.getAvatar());
        return singleResult(avatarService.set(avatar, account));
    }

    @RequestMapping(value = "{uuid}", method = RequestMethod.GET)
    public ApiResult getById(@PathVariable final UUID uuid) {
        Avatar avatarFromDb = avatarService.get(uuid);
        if (avatarFromDb == null) {
            throw new EntityNotExistException("Avatar not exist");
        }
        return ApiUtils.singleResult(avatarFromDb);
    }

    @RequestMapping(value = "", method = RequestMethod.DELETE)
    @ResponseStatus(value = HttpStatus.NO_CONTENT)
    public void delete(@AuthenticationPrincipal final User user) {
        Account account = accountService.getByEmail(user.getUsername());
        if (account.getAvatar() != null) {
            deleteAvatarFromStorage(account.getAvatar());
            avatarService.delete(account.getAvatar());
        }
    }

    @RequestMapping(value = "/file/{name}", method = RequestMethod.GET, produces = "image/jpg")
    public byte[] getByName(@PathVariable final String name) {
        return ImageUtils.toByteArray(new File(Constants.AVATAR_PATH + name));
    }

    private void deleteAvatarFromStorage(Avatar avatar) {
        if (avatar == null) {
            return;
        }

        ImageUtils.deleteImages(Constants.AVATAR_PATH, avatar.getOriginalPath(), avatar.getThumbnailPath());
    }
}
