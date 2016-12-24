package me.academeg.api.service.impl;

import me.academeg.api.entity.Tag;
import me.academeg.api.exception.entity.TagExistException;
import me.academeg.api.exception.entity.TagNotExistException;
import me.academeg.api.repository.TagRepository;
import me.academeg.api.service.ArticleService;
import me.academeg.api.service.TagService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
    private ArticleService articleService;

    @Autowired
    public TagServiceImpl(
        TagRepository tagRepository,
        ArticleService articleService
    ) {
        this.tagRepository = tagRepository;
        this.articleService = articleService;
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
        Tag tag = getById(id);
        if (tag == null) {
            throw new TagNotExistException();
        }
        tag.getArticles().forEach(article -> {
            article.getTags().remove(tag);
            articleService.update(article);
        });
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
        if (getById(tag.getId()) == null) {
            throw new TagNotExistException();
        }
        Tag tagFromDb = getByValue(tag.getValue());
        if (tagFromDb != null && !tagFromDb.getId().equals(tag.getId())) {
            throw new TagExistException();
        }
        return tagRepository.save(tag);
    }
}
