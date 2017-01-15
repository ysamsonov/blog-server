package me.academeg.dal.repository;

import me.academeg.dal.domain.Tag;

import java.util.UUID;

/**
 * TagRepository Repository
 *
 * @author Yuriy A. Samsonov <yuriy.samsonov96@gmail.com>
 * @version 1.0
 */
public interface TagRepository extends BaseRepository<Tag, UUID> {

    Tag getByValue(String value);
}
