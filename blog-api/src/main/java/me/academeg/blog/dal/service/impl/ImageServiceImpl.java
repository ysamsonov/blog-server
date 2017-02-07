package me.academeg.blog.dal.service.impl;

import lombok.Getter;
import me.academeg.blog.api.exception.EntityNotExistException;
import me.academeg.blog.dal.domain.Image;
import me.academeg.blog.dal.repository.ImageRepository;
import me.academeg.blog.dal.service.ImageService;
import me.academeg.blog.dal.utils.ImageUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.util.Optional;
import java.util.UUID;

/**
 * ImageServiceImpl
 *
 * @author Yuriy A. Samsonov <y.samsonov@erpscan.com>
 * @version 1.0
 */
@Service
public class ImageServiceImpl implements ImageService {

    @Value("${me.academeg.blog.images.path:image/}")
    @Getter
    private String path;

    private ImageRepository imageRepository;

    @Autowired
    public ImageServiceImpl(ImageRepository imageRepository) {
        this.imageRepository = imageRepository;
    }

    @Override
    public Image create(MultipartFile file) {
        Image image = new Image();
        image.setOriginalPath(ImageUtils.saveImage(path, file));
        image.setThumbnailPath(ImageUtils.compressImage(image.getOriginalPath(), path));
        return imageRepository.save(image);
    }

    @Override
    public Image update(Image image) {
        return imageRepository.save(image);
    }

    @Override
    public Image getById(UUID id) {
        return imageRepository.findOne(id);
    }

    @Override
    public void delete(UUID id) {
        Image image = Optional
            .ofNullable(imageRepository.findOne(id))
            .orElseThrow(() -> new EntityNotExistException("Image with id %s not exist", id));

        if (image.getArticle() != null) {
            image.getArticle().getImages().remove(image);
        }
        ImageUtils.deleteImages(path, image.getOriginalPath(), image.getThumbnailPath());
        imageRepository.delete(image);
    }

    @Override
    public byte[] getFile(String name) {
        return ImageUtils.toByteArray(new File(path + name));
    }
}
