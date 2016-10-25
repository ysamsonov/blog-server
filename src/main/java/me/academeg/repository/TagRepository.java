package me.academeg.repository;

import me.academeg.entity.Tag;
import org.springframework.data.repository.CrudRepository;

import java.util.UUID;

/**
 * TagRepository Repository
 *
 * @author Yuriy A. Samsonov <yuriy.samsonov96@gmail.com>
 * @version 1.0
 */
public interface TagRepository extends CrudRepository<Tag, UUID> {
}
