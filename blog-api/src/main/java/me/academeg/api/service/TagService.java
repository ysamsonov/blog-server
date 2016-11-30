package me.academeg.api.service;

import me.academeg.api.entity.Tag;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

/**
 * TagService
 *
 * @author Yuriy A. Samsonov <y.samsonov@erpscan.com>
 * @version 1.0
 */
public interface TagService {

    Tag add(Tag tag);

    void delete(UUID uuid);

    Tag getByUuid(UUID uuid);

    Tag getByValue(String value);

    Page<Tag> getPerPage(Pageable pageable);

    Tag edit(Tag tag);
}
