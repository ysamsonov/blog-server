package me.academeg.blog.api.controller;

import lombok.extern.slf4j.Slf4j;
import me.academeg.blog.api.common.ApiResult;
import me.academeg.blog.dal.domain.Tag;
import me.academeg.blog.dal.service.TagService;
import me.academeg.blog.security.RoleConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

import static me.academeg.blog.api.utils.ApiUtils.*;

/**
 * TagController Controller
 *
 * @author Yuriy A. Samsonov <y.samsonov@erpscan.com>
 * @version 1.0
 */
@RestController
@RequestMapping("/api/tags")
@Validated
@Slf4j
public class TagController {

    private final TagService tagService;
    private final Class resourceClass;

    @Autowired
    public TagController(final TagService tagService) {
        this.tagService = tagService;
        this.resourceClass = Tag.class;
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public ApiResult getById(@PathVariable final UUID id) {
        log.info("/GET method invoked for {} id {}", resourceClass.getSimpleName(), id);
        return singleResult(tagService.getById(id));
    }

    @RequestMapping(value = "/list", method = RequestMethod.GET)
    public ApiResult getList(
        @RequestParam(required = false) final Integer page,
        @RequestParam(required = false) final Integer limit,
        @RequestParam(required = false) final String orderBy
    ) {
        log.info("/LIST method invoked for {}", resourceClass.getSimpleName());
        return listResult(tagService.getPage(createPageRequest(limit, page, orderBy)));
    }

    @RequestMapping(value = "", method = RequestMethod.POST)
    public ApiResult create(@Validated @RequestBody final Tag tag) {
        log.info("/CREATE method invoked for {}", resourceClass.getSimpleName());
        return singleResult(tagService.create(tag));
    }

    @Secured({RoleConstants.ADMIN, RoleConstants.MODERATOR})
    @RequestMapping(value = "/{id}", method = RequestMethod.PUT)
    public ApiResult update(
        @PathVariable final UUID id,
        @RequestBody final Tag tag
    ) {
        log.info("/UPDATE method invoked for {} id {}", resourceClass.getSimpleName(), id);
        tag.setId(id);
        return singleResult(tagService.update(tag));
    }

    @Secured({RoleConstants.ADMIN, RoleConstants.MODERATOR})
    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    public ApiResult delete(final @PathVariable UUID id) {
        log.info("/DELETE method invoked for {} id {}", resourceClass.getSimpleName(), id);
        tagService.delete(id);
        return okResult();
    }
}
