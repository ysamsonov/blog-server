package me.academeg.api.controller;

import me.academeg.api.Constants;
import me.academeg.api.common.ApiResult;
import me.academeg.api.entity.Account;
import me.academeg.api.entity.Image;
import me.academeg.api.exception.EntityNotExistException;
import me.academeg.api.exception.entity.AccountPermissionException;
import me.academeg.api.exception.entity.FileFormatException;
import me.academeg.api.exception.entity.ImageNotExistException;
import me.academeg.api.service.AccountService;
import me.academeg.api.service.ImageService;
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
        Image imageFromDb = imageService.getByUuid(id);
        if (imageFromDb == null) {
            throw new EntityNotExistException("Image not exist");
        }
        return singleResult(imageFromDb);
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    public ApiResult delete(@AuthenticationPrincipal final User user, @PathVariable final UUID id) {
        Image imageFromDb = imageService.getByUuid(id);
        if (imageFromDb == null) {
            throw new ImageNotExistException();
        }

        Account authAcc = accountService.getByEmail(user.getUsername());
        if (imageFromDb.getArticle() == null) {
            throw new ImageNotExistException();
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
