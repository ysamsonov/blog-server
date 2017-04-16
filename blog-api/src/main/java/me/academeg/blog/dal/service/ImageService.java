package me.academeg.blog.dal.service;

import me.academeg.blog.api.exception.BlogEntityNotExistException;
import me.academeg.blog.dal.domain.Image;
import me.academeg.blog.dal.repository.ImageRepository;
import me.academeg.blog.dal.utils.ImageUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.util.Optional;
import java.util.UUID;

/**
 * @author Yuriy A. Samsonov <y.samsonov@erpscan.com>
 * @version 1.0
 */
@Service
@Transactional
public class ImageService {

    @Value("${me.academeg.blog.images.path:image/}")
    private String path;

    private ImageRepository imageRepository;

    @Autowired
    public ImageService(ImageRepository imageRepository) {
        this.imageRepository = imageRepository;
    }

    public Image create(MultipartFile file) {
        Image image = new Image();
        image.setOriginalPath(ImageUtils.saveImage(path, file));
        image.setThumbnailPath(ImageUtils.compressImage(image.getOriginalPath(), path));
        return imageRepository.save(image);
    }

    public Image update(Image image) {
        return imageRepository.save(image);
    }

    @Transactional(readOnly = true)
    public Image getById(UUID id) {
        return imageRepository.findOne(id);
    }

    public void delete(UUID id) {
        Image image = Optional
            .ofNullable(imageRepository.findOne(id))
            .orElseThrow(() -> new BlogEntityNotExistException("Image with id %s not exist", id));

        if (image.getArticle() != null) {
            image.setArticle(null);
        }
        ImageUtils.deleteImages(path, image.getOriginalPath(), image.getThumbnailPath());
        imageRepository.delete(image);
    }

    @Transactional(readOnly = true)
    public byte[] getFile(String name) {
        return ImageUtils.toByteArray(new File(path + name));
    }
}
