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

    Image add(Image image);

    Image edit(Image image);

    Image getByUuid(UUID uuid);

    void delete(Image image);

    void delete(UUID uuid);
}
