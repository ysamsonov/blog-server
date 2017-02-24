package me.academeg.blog.api.controller;

import lombok.extern.slf4j.Slf4j;
import me.academeg.blog.api.common.ApiResult;
import me.academeg.blog.api.exception.BlogEntityNotExistException;
import me.academeg.blog.api.exception.BlogFileFormatException;
import me.academeg.blog.dal.domain.Account;
import me.academeg.blog.dal.domain.Avatar;
import me.academeg.blog.dal.service.AccountService;
import me.academeg.blog.dal.service.AvatarService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.User;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Optional;
import java.util.UUID;

import static me.academeg.blog.api.utils.ApiUtils.okResult;
import static me.academeg.blog.api.utils.ApiUtils.singleResult;

/**
 * AvatarController Controller
 *
 * @author Yuriy A. Samsonov <yuriy.samsonov96@gmail.com>
 * @version 1.0
 */
@RestController
@RequestMapping("/api/avatars")
@Validated
@Slf4j
public class AvatarController {
    private final AvatarService avatarService;
    private final AccountService accountService;
    private final Class resourceClass;

    @Autowired
    public AvatarController(
        AvatarService avatarService,
        AccountService accountService
    ) {
        this.avatarService = avatarService;
        this.accountService = accountService;
        this.resourceClass = Avatar.class;
    }

    @RequestMapping(value = "", method = RequestMethod.POST)
    public ApiResult create(
        @AuthenticationPrincipal final User user,
        @RequestParam final MultipartFile image
    ) {
        log.info("/CREATE method invoked for {}", resourceClass.getSimpleName());
        if (!image.getContentType().startsWith("image/")) {
            throw new BlogFileFormatException("You can upload only images");
        }

        return singleResult(avatarService.create(image, accountService.getByEmail(user.getUsername())));
    }

    @RequestMapping(value = "{id}", method = RequestMethod.GET)
    public ApiResult getById(@PathVariable final UUID id) {
        log.info("/GET method invoked for {} id {}", resourceClass.getSimpleName(), id);
        return singleResult(
            Optional
                .ofNullable(avatarService.getById(id))
                .<BlogEntityNotExistException>orElseThrow(
                    () -> new BlogEntityNotExistException("Avatar with id %s not exist", id))
        );
    }

    @RequestMapping(value = "", method = RequestMethod.DELETE)
    public ApiResult delete(@AuthenticationPrincipal final User user) {
        log.info("/DELETE method invoked for {}", resourceClass.getSimpleName());
        Account account = accountService.getByEmail(user.getUsername());
        if (account.getAvatar() != null) {
            avatarService.delete(account.getAvatar().getId());
        }
        return okResult();
    }

    @RequestMapping(value = "/file/{name}", method = RequestMethod.GET, produces = "image/jpg")
    public byte[] getByName(@PathVariable final String name) {
        log.info("/FILE method invoked for {} name {}", resourceClass.getSimpleName(), name);
        return avatarService.getFile(name);
    }
}
