package me.academeg.dal.service;

import me.academeg.dal.domain.Image;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

/**
 * ImageService
 *
 * @author Yuriy A. Samsonov <y.samsonov@erpscan.com>
 * @version 1.0
 */
public interface ImageService {

    Image create(MultipartFile file);

    void delete(UUID id);

    Image getById(UUID id);

    Image update(Image image);
}
