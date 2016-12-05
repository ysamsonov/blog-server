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

/**
 * ImageController Controller
 *
 * @author Yuriy A. Samsonov <y.samsonov@erpscan.com>
 * @version 1.0
 */
@RestController
@RequestMapping("/api/image")
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
    public ApiResult create(@RequestParam(name = "image") final MultipartFile file) {
        if (!file.getContentType().startsWith("image/")) {
            throw new FileFormatException("You can upload only images");
        }

        File imagesDir = new File(Constants.IMAGE_PATH);
        if (!imagesDir.exists()) {
            imagesDir.mkdir();
        }

        Image image = new Image();
        String originalImageName = ImageUtils.saveImage(imagesDir, file);
        image.setOriginalPath(Constants.IMAGE_PATH + originalImageName);
        String thumbnailImageName = ImageUtils.compressImage(new File(imagesDir, originalImageName), imagesDir);
        image.setThumbnailPath(Constants.IMAGE_PATH + thumbnailImageName);

        return ApiUtils.singleResult(imageService.add(image));
    }

    @RequestMapping(value = "{uuid}", method = RequestMethod.GET)
    public ApiResult getById(@PathVariable final UUID uuid) {
        Image imageFromDb = imageService.getByUuid(uuid);
        if (imageFromDb == null) {
            throw new EntityNotExistException("Image not exist");
        }
        return ApiUtils.singleResult(imageFromDb);
    }

    @RequestMapping(value = "/{uuid}", method = RequestMethod.DELETE)
    @ResponseStatus(value = HttpStatus.NO_CONTENT)
    public void delete(@AuthenticationPrincipal final User user, @PathVariable final UUID uuid) {
        Image imageFromDb = imageService.getByUuid(uuid);
        if (imageFromDb == null) {
            throw new ImageNotExistException();
        }

        Account authAcc = accountService.getByEmail(user.getUsername());
        if (imageFromDb.getArticle() == null) {
            return;
        }

        if (!imageFromDb.getArticle().getAuthor().getId().equals(authAcc.getId())) {
            throw new AccountPermissionException();
        }

        deleteImageFromStorage(imageFromDb);
        imageService.delete(imageFromDb);
    }

    @RequestMapping(value = "/file/{name}", method = RequestMethod.GET, produces = "image/jpg")
    public byte[] getByName(@PathVariable final String name) {
        return ImageUtils.toByteArray(new File(Constants.IMAGE_PATH + name));
    }

    private void deleteImageFromStorage(Image image) {
        if (image == null) {
            return;
        }
        new File(image.getOriginalPath()).delete();
        new File(image.getThumbnailPath()).delete();
    }
}
