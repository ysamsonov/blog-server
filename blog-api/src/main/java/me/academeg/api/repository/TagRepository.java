package me.academeg.api.repository;

import me.academeg.api.entity.Tag;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.UUID;

/**
 * TagRepository Repository
 *
 * @author Yuriy A. Samsonov <yuriy.samsonov96@gmail.com>
 * @version 1.0
 */
public interface TagRepository extends PagingAndSortingRepository<Tag, UUID> {

    Tag getByValue(String value);
}
