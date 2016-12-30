package me.academeg.api.controller;

import me.academeg.api.Constants;
import me.academeg.api.common.ApiResult;
import me.academeg.api.entity.Account;
import me.academeg.api.exception.entity.FileFormatException;
import me.academeg.api.service.AccountService;
import me.academeg.api.service.AvatarService;
import me.academeg.api.utils.ImageUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.User;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.util.UUID;

import static me.academeg.api.utils.ApiUtils.okResult;
import static me.academeg.api.utils.ApiUtils.singleResult;

/**
 * AvatarController Controller
 *
 * @author Yuriy A. Samsonov <yuriy.samsonov96@gmail.com>
 * @version 1.0
 */
@RestController
@RequestMapping("/api/avatars")
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

        return singleResult(avatarService.create(image, accountService.getByEmail(user.getUsername())));
    }

    @RequestMapping(value = "{id}", method = RequestMethod.GET)
    public ApiResult getById(@PathVariable final UUID id) {
        return singleResult(avatarService.getById(id));
    }

    @RequestMapping(value = "", method = RequestMethod.DELETE)
    public ApiResult delete(@AuthenticationPrincipal final User user) {
        Account account = accountService.getByEmail(user.getUsername());
        if (account.getAvatar() != null) {
            avatarService.delete(account.getAvatar().getId());
        }
        return okResult();
    }

    @RequestMapping(value = "/file/{name}", method = RequestMethod.GET, produces = "image/jpg")
    public byte[] getByName(@PathVariable final String name) {
        return ImageUtils.toByteArray(new File(Constants.AVATAR_PATH + name));
    }
}
