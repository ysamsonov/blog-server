package me.academeg.api.controller;

import me.academeg.api.Constants;
import me.academeg.api.common.ApiResult;
import me.academeg.dal.domain.Account;
import me.academeg.dal.domain.Image;
import me.academeg.api.exception.EntityNotExistException;
import me.academeg.api.exception.AccountPermissionException;
import me.academeg.api.exception.FileFormatException;
import me.academeg.dal.service.AccountService;
import me.academeg.dal.service.ImageService;
import me.academeg.dal.utils.ImageUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.User;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.util.Optional;
import java.util.UUID;

import static me.academeg.api.utils.ApiUtils.okResult;
import static me.academeg.api.utils.ApiUtils.singleResult;

/**
 * ImageController Controller
 *
 * @author Yuriy A. Samsonov <y.samsonov@erpscan.com>
 * @version 1.0
 */
@RestController
@RequestMapping("/api/images")
@Validated
public class ImageController {
    private final ImageService imageService;
    private final AccountService accountService;

    @Autowired
    public ImageController(ImageService imageService, AccountService accountService) {
        this.imageService = imageService;
        this.accountService = accountService;
    }

    @RequestMapping(value = "", method = RequestMethod.POST)
    public ApiResult create(@RequestParam(name = "image") final MultipartFile image) {
        if (!image.getContentType().startsWith("image/")) {
            throw new FileFormatException("You can upload only images");
        }

        return singleResult(imageService.create(image));
    }

    @RequestMapping(value = "{id}", method = RequestMethod.GET)
    public ApiResult getById(@PathVariable final UUID id) {
        return singleResult(
            Optional
                .ofNullable(imageService.getById(id))
                .<EntityNotExistException>orElseThrow(
                    () -> new EntityNotExistException("Image with id %s not exist", id))
        );
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    public ApiResult delete(@AuthenticationPrincipal final User user, @PathVariable final UUID id) {
        Image imageFromDb = Optional
            .ofNullable(imageService.getById(id))
            .orElseThrow(() -> new EntityNotExistException("Image with id %s not exist", id));

        Account authAcc = accountService.getByEmail(user.getUsername());
        if (imageFromDb.getArticle() == null) {
            throw new EntityNotExistException("Image with id %s not exist", id);
        }

        if (!imageFromDb.getArticle().getAuthor().getId().equals(authAcc.getId())) {
            throw new AccountPermissionException();
        }

        imageService.delete(imageFromDb.getId());
        return okResult();
    }

    @RequestMapping(value = "/file/{name}", method = RequestMethod.GET, produces = "image/jpg")
    public byte[] getByName(@PathVariable final String name) {
        return ImageUtils.toByteArray(new File(Constants.IMAGE_PATH + name));
    }
}
