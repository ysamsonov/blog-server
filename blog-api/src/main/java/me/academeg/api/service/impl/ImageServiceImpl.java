package me.academeg.api.service.impl;

import me.academeg.api.entity.Image;
import me.academeg.api.exception.entity.ImageNotExistException;
import me.academeg.api.repository.ImageRepository;
import me.academeg.api.service.ImageService;
import me.academeg.api.utils.ImageUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

import static me.academeg.api.Constants.AVATAR_PATH;
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
    public Image getByUuid(UUID id) {
        return imageRepository.findOne(id);
    }

    @Transactional
    @Override
    public void delete(UUID id) {
        Image image = imageRepository.findOne(id);
        if (image == null || image.getArticle() == null) {
            throw new ImageNotExistException();
        }
        ImageUtils.deleteImages(AVATAR_PATH, image.getOriginalPath(), image.getThumbnailPath());
        imageRepository.delete(image);
    }
}
