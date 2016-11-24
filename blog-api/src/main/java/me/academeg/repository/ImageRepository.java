package me.academeg.repository;

import me.academeg.entity.Image;
import org.springframework.data.repository.CrudRepository;

import java.util.UUID;

/**
 * ImageRepository Repository
 *
 * @author Yuriy A. Samsonov <yuriy.samsonov96@gmail.com>
 * @version 1.0
 */
public interface ImageRepository extends CrudRepository<Image, UUID> {
}
