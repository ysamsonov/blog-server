package me.academeg.api.controller;

import me.academeg.api.common.ApiResult;
import me.academeg.api.entity.Account;
import me.academeg.api.entity.AccountRole;
import me.academeg.api.entity.Tag;
import me.academeg.api.exception.entity.AccountPermissionException;
import me.academeg.api.exception.entity.TagExistException;
import me.academeg.api.exception.entity.TagNotExistException;
import me.academeg.api.service.AccountService;
import me.academeg.api.service.TagService;
import me.academeg.api.utils.ApiUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.User;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

/**
 * TagController Controller
 *
 * @author Yuriy A. Samsonov <y.samsonov@erpscan.com>
 * @version 1.0
 */
@RestController
@RequestMapping("/api/tags")
@Validated
public class TagController {

    private final TagService tagService;
    private final AccountService accountService;

    @Autowired
    public TagController(TagService tagService, AccountService accountService) {
        this.tagService = tagService;
        this.accountService = accountService;
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public ApiResult getById(@PathVariable final UUID id) {
        return ApiUtils.singleResult(tagService.getById(id));
    }

    @RequestMapping(value = "/list", method = RequestMethod.GET)
    public ApiResult getList(final Integer page, final Integer limit) {
        return ApiUtils.listResult(tagService.getPage(ApiUtils.createPageRequest(limit, page, null)));
    }

    @RequestMapping(value = "", method = RequestMethod.POST)
    public ApiResult create(@Validated @RequestBody final Tag tag) {
        return ApiUtils.singleResult(tagService.create(tag));
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.PUT)
    public ApiResult update(
        @AuthenticationPrincipal final User user,
        @PathVariable final UUID id,
        @RequestBody final Tag tag
    ) {
        Account authAccount = accountService.getByEmail(user.getUsername());
        if (!authAccount.getAuthority().equals(AccountRole.MODERATOR)
            && !authAccount.getAuthority().equals(AccountRole.ADMIN)) {
            throw new AccountPermissionException();
        }

        Tag tagFromDbUuid = tagService.getById(id);
        if (tagService.getById(id) == null) {
            throw new TagNotExistException();
        }

        Tag tagFromDbValue = tagService.getByValue(tag.getValue());
        if (tagFromDbValue != null && !tagFromDbValue.getId().equals(id)) {
            throw new TagExistException();
        }

        tagFromDbUuid.setValue(tag.getValue());
        return ApiUtils.singleResult(tagService.update(tagFromDbUuid));
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    @ResponseStatus(value = HttpStatus.NO_CONTENT)
    public void delete(@AuthenticationPrincipal final User user, final @PathVariable UUID id) {
        Account authAccount = accountService.getByEmail(user.getUsername());
        if (!authAccount.getAuthority().equals(AccountRole.MODERATOR)
            && !authAccount.getAuthority().equals(AccountRole.ADMIN)) {
            throw new AccountPermissionException();
        }

        tagService.delete(id);
    }
}
