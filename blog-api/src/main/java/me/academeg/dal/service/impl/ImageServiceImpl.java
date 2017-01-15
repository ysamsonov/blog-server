package me.academeg.dal.service.impl;

import me.academeg.dal.domain.Image;
import me.academeg.api.exception.EntityNotExistException;
import me.academeg.dal.repository.ImageRepository;
import me.academeg.dal.service.ImageService;
import me.academeg.dal.utils.ImageUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

import static me.academeg.api.Constants.IMAGE_PATH;

/**
 * ImageServiceImpl
 *
 * @author Yuriy A. Samsonov <y.samsonov@erpscan.com>
 * @version 1.0
 */
@Service
public class ImageServiceImpl implements ImageService {

    private ImageRepository imageRepository;

    @Autowired
    public ImageServiceImpl(ImageRepository imageRepository) {
        this.imageRepository = imageRepository;
    }

    @Override
    public Image create(MultipartFile file) {
        Image image = new Image();
        image.setOriginalPath(ImageUtils.saveImage(IMAGE_PATH, file));
        image.setThumbnailPath(ImageUtils.compressImage(image.getOriginalPath(), IMAGE_PATH));
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
        Image image = imageRepository.findOne(id);
        if (image == null || image.getArticle() == null) {
            throw new EntityNotExistException("Image with id %s not exist", id);
        }
        image.getArticle().getImages().remove(image);
        ImageUtils.deleteImages(IMAGE_PATH, image.getOriginalPath(), image.getThumbnailPath());
        imageRepository.delete(image);
    }
}
