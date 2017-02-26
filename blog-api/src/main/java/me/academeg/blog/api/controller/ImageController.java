package me.academeg.blog.api.controller;

import lombok.extern.slf4j.Slf4j;
import me.academeg.blog.api.common.ApiResult;
import me.academeg.blog.dal.domain.Image;
import me.academeg.blog.dal.service.ImageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

import static me.academeg.blog.api.utils.ApiUtils.okResult;
import static me.academeg.blog.api.utils.ApiUtils.singleResult;

/**
 * ImageController Controller
 *
 * @author Yuriy A. Samsonov <y.samsonov@erpscan.com>
 * @version 1.0
 */
@RestController
@RequestMapping("/api/images")
@Slf4j
public class ImageController {

    private final ImageService imageService;
    private final Class resourceClass;

    @Autowired
    public ImageController(final ImageService imageService) {
        this.imageService = imageService;
        this.resourceClass = Image.class;
    }

    @RequestMapping(value = "", method = RequestMethod.POST, consumes = MediaType.IMAGE_JPEG_VALUE)
    public ApiResult create(@RequestParam(name = "image") final MultipartFile image) {
        log.info("/CREATE method invoked for {}", resourceClass.getSimpleName());
        return singleResult(imageService.create(image));
    }

    @RequestMapping(value = "{id}", method = RequestMethod.GET)
    public ApiResult getById(@PathVariable final UUID id) {
        log.info("/GET method invoked for {} id {}", resourceClass.getSimpleName(), id);
        return singleResult(imageService.getById(id));
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    public ApiResult delete(@PathVariable final UUID id) {
        log.info("/DELETE method invoked for {} id {}", resourceClass.getSimpleName(), id);
        imageService.delete(id);
        return okResult();
    }

    @RequestMapping(value = "/file/{name}", method = RequestMethod.GET, produces = MediaType.IMAGE_JPEG_VALUE)
    public byte[] getByName(@PathVariable final String name) {
        log.info("/FILE method invoked for {} name {}", resourceClass.getSimpleName(), name);
        return imageService.getFile(name);
    }
}
