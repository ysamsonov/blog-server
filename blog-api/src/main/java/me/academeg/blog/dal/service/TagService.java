package me.academeg.blog.dal.service;

import me.academeg.blog.api.exception.BlogEntityExistException;
import me.academeg.blog.api.exception.BlogEntityNotExistException;
import me.academeg.blog.dal.domain.Tag;
import me.academeg.blog.dal.repository.TagRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.Optional;
import java.util.UUID;

/**
 * @author Yuriy A. Samsonov <y.samsonov@erpscan.com>
 * @version 1.0
 */
@Service
@Transactional
public class TagService {

    private TagRepository tagRepository;

    @Autowired
    public TagService(TagRepository tagRepository) {
        this.tagRepository = tagRepository;
    }

    public Tag create(Tag tag) {
        Tag newTag = getByValue(tag.getValue());
        if (newTag != null) {
            return newTag;
        }

        tag.setValue(tag.getValue().toLowerCase());
        return tagRepository.saveAndFlush(tag);
    }

    public void delete(UUID id) {
        Tag tag = Optional
            .ofNullable(getById(id))
            .orElseThrow(() -> new BlogEntityNotExistException("Tag with id %s not exist"));

        tag.setArticles(Collections.emptyList());
        tagRepository.delete(tagRepository.save(tag));
    }

    @Transactional(readOnly = true)
    public Tag getById(UUID id) {
        return tagRepository.findOne(id);
    }

    @Transactional(readOnly = true)
    public Tag getByValue(String value) {
        return tagRepository.getByValue(value.toLowerCase());
    }

    @Transactional(readOnly = true)
    public Page<Tag> getPage(Pageable pageable) {
        return tagRepository.findAll(pageable);
    }

    public Tag update(Tag tag) {
        tag.setValue(tag.getValue().toLowerCase());
        Tag tagFromDb = getById(tag.getId());
        if (tagFromDb == null) {
            throw new BlogEntityNotExistException("Tag with id %s not exist", tag.getId());
        }
        Tag tagFromDbByValue = getByValue(tag.getValue());
        if (tagFromDbByValue != null) {
            throw new BlogEntityExistException("Tag with value %s already exist", tag.getValue());
        }
        tagFromDb.setValue(tag.getValue());
        return tagRepository.saveAndFlush(tagFromDb);
    }
}
