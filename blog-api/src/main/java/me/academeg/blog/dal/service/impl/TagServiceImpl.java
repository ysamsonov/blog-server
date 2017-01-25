package me.academeg.blog.dal.service.impl;

import me.academeg.blog.api.exception.EntityNotExistException;
import me.academeg.blog.dal.repository.TagRepository;
import me.academeg.blog.dal.domain.Tag;
import me.academeg.blog.api.exception.EntityExistException;
import me.academeg.blog.dal.service.TagService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
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
    public Tag create(Tag tag) {
        Tag newTag = getByValue(tag.getValue());
        if (newTag != null) {
            return newTag;
        }

        newTag = new Tag();
        newTag.setValue(tag.getValue().toLowerCase());
        return tagRepository.save(newTag);
    }

    @Transactional
    @Override
    public void delete(UUID id) {
        Tag tag = Optional
            .ofNullable(getById(id))
            .orElseThrow(() -> new EntityNotExistException("Tag with id %s not exist"));

        tag.getArticles().forEach(article -> article.getTags().remove(tag));
        tag.setArticles(null);
        tagRepository.delete(tagRepository.save(tag));
    }

    @Override
    public Tag getById(UUID id) {
        return tagRepository.findOne(id);
    }

    @Override
    public Tag getByValue(String value) {
        return tagRepository.getByValue(value.toLowerCase());
    }

    @Override
    public Page<Tag> getPage(Pageable pageable) {
        return tagRepository.findAll(pageable);
    }

    @Transactional
    @Override
    public Tag update(Tag tag) {
        tag.setValue(tag.getValue().toLowerCase());
        Tag tagFromDb = getById(tag.getId());
        if (tagFromDb == null) {
            throw new EntityNotExistException("Tag with id %s not exist", tag.getId());
        }
        Tag tagFromDbByValue = getByValue(tag.getValue());
        if (tagFromDbByValue != null) {
            throw new EntityExistException("Tag with value %s already exist", tag.getValue());
        }
        tagFromDb.setValue(tag.getValue());
        return tagRepository.save(tagFromDb);
    }
}
