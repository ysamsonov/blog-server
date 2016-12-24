package me.academeg.api.service;

import me.academeg.api.entity.Image;

import java.util.UUID;

/**
 * ImageService
 *
 * @author Yuriy A. Samsonov <y.samsonov@erpscan.com>
 * @version 1.0
 */
public interface ImageService {

    Image create(Image image);

    void delete(UUID id);

    Image getByUuid(UUID id);

    Image update(Image image);
}
