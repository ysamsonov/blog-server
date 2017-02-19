package me.academeg.blog.api.controller;

import lombok.extern.slf4j.Slf4j;
import me.academeg.blog.api.common.ApiResult;
import me.academeg.blog.api.exception.AccountPermissionException;
import me.academeg.blog.api.exception.EntityNotExistException;
import me.academeg.blog.dal.domain.Account;
import me.academeg.blog.dal.domain.AccountRole;
import me.academeg.blog.dal.domain.Tag;
import me.academeg.blog.dal.service.AccountService;
import me.academeg.blog.dal.service.TagService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.User;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;
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
    private final AccountService accountService;
    private final Class resourceClass;

    @Autowired
    public TagController(TagService tagService, AccountService accountService) {
        this.tagService = tagService;
        this.accountService = accountService;
        this.resourceClass = Tag.class;
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public ApiResult getById(@PathVariable final UUID id) {
        log.info("/GET method invoked for {} id {}", resourceClass.getSimpleName(), id);
        //noinspection RedundantTypeArguments
        return singleResult(
            Optional
                .ofNullable(tagService.getById(id))
                .<EntityNotExistException>orElseThrow(() -> new EntityNotExistException("Tag with id %s not exist", id))
        );
    }

    @RequestMapping(value = "/list", method = RequestMethod.GET)
    public ApiResult getList(final Integer page, final Integer limit) {
        log.info("/LIST method invoked for {}", resourceClass.getSimpleName());
        return listResult(tagService.getPage(createPageRequest(limit, page, null)));
    }

    @RequestMapping(value = "", method = RequestMethod.POST)
    public ApiResult create(@Validated @RequestBody final Tag tag) {
        log.info("/CREATE method invoked for {}", resourceClass.getSimpleName());
        return singleResult(tagService.create(tag));
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.PUT)
    public ApiResult update(
        @AuthenticationPrincipal final User user,
        @PathVariable final UUID id,
        @RequestBody final Tag tag
    ) {
        log.info("/UPDATE method invoked for {} id {}", resourceClass.getSimpleName(), id);
        Account authAccount = accountService.getByEmail(user.getUsername());
        if (!authAccount.hasRole(AccountRole.MODERATOR)
            && !authAccount.hasRole(AccountRole.ADMIN)) {
            throw new AccountPermissionException();
        }
        tag.setId(id);
        return singleResult(tagService.update(tag));
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    public ApiResult delete(@AuthenticationPrincipal final User user, final @PathVariable UUID id) {
        log.info("/DELETE method invoked for {} id {}", resourceClass.getSimpleName(), id);
        Account authAccount = accountService.getByEmail(user.getUsername());
        if (!authAccount.hasRole(AccountRole.MODERATOR)
            && !authAccount.hasRole(AccountRole.ADMIN)) {
            throw new AccountPermissionException("Only admin/moderator can delete the tag");
        }
        tagService.delete(id);
        return okResult();
    }
}
