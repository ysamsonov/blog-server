package me.academeg.api.service.impl;

import me.academeg.api.entity.Tag;
import me.academeg.api.repository.TagRepository;
import me.academeg.api.service.TagService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.UUID;

/**
 * TagServiceImpl
 *
 * @author Yuriy A. Samsonov <y.samsonov@erpscan.com>
 * @version 1.0
 */
@Service
public class TagServiceImpl implements TagService {

    private TagRepository tagRepository;

    @Autowired
    public TagServiceImpl(TagRepository tagRepository) {
        this.tagRepository = tagRepository;
    }

    @Override
    public Tag add(Tag tag) {
        tag.setValue(tag.getValue().toLowerCase());
        return tagRepository.save(tag);
    }

    @Override
    public void delete(UUID uuid) {
        tagRepository.delete(uuid);
    }

    @Override
    public Tag getByUuid(UUID uuid) {
        return tagRepository.findOne(uuid);
    }

    @Override
    public Tag getByValue(String value) {
        return tagRepository.getByValue(value.toLowerCase());
    }

    @Override
    public Page<Tag> getPerPage(Pageable pageable) {
        return tagRepository.findAll(pageable);
    }

    @Override
    public Tag edit(Tag tag) {
        tag.setValue(tag.getValue().toLowerCase());
        return tagRepository.save(tag);
    }
}
