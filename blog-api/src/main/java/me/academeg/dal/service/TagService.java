package me.academeg.dal.service;

import me.academeg.dal.domain.Tag;
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

    Tag create(Tag tag);

    void delete(UUID id);

    Tag getById(UUID id);

    Tag getByValue(String value);

    Page<Tag> getPage(Pageable pageable);

    Tag update(Tag tag);
}
