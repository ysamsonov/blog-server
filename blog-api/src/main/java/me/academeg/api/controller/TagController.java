package me.academeg.api.controller;

import me.academeg.api.common.ApiResult;
import me.academeg.api.entity.Account;
import me.academeg.api.entity.AccountRole;
import me.academeg.api.entity.Tag;
import me.academeg.api.exception.EntityNotExistException;
import me.academeg.api.exception.AccountPermissionException;
import me.academeg.api.service.AccountService;
import me.academeg.api.service.TagService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.User;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;
import java.util.UUID;

import static me.academeg.api.utils.ApiUtils.*;

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
        return singleResult(
            Optional
                .ofNullable(tagService.getById(id))
                .<EntityNotExistException>orElseThrow(() -> new EntityNotExistException("Tag with id %s not exist", id))
        );
    }

    @RequestMapping(value = "/list", method = RequestMethod.GET)
    public ApiResult getList(final Integer page, final Integer limit) {
        return listResult(tagService.getPage(createPageRequest(limit, page, null)));
    }

    @RequestMapping(value = "", method = RequestMethod.POST)
    public ApiResult create(@Validated @RequestBody final Tag tag) {
        return singleResult(tagService.create(tag));
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

        tag.setId(id);
        return singleResult(tagService.update(tag));
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    public ApiResult delete(@AuthenticationPrincipal final User user, final @PathVariable UUID id) {
        Account authAccount = accountService.getByEmail(user.getUsername());
        if (!authAccount.getAuthority().equals(AccountRole.MODERATOR)
            && !authAccount.getAuthority().equals(AccountRole.ADMIN)) {
            throw new AccountPermissionException("Only admin/moderator can delete the tag");
        }

        tagService.delete(id);
        return okResult();
    }
}
