package me.academeg.api.service.impl;

import me.academeg.api.entity.Image;
import me.academeg.api.repository.ImageRepository;
import me.academeg.api.service.ImageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.UUID;

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
    public Image create(Image image) {
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

    @Override
    public void delete(UUID id) {
        imageRepository.delete(id);
    }
}
