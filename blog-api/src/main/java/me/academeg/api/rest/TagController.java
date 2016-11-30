package me.academeg.api.rest;

import me.academeg.common.ApiResult;
import me.academeg.entity.Account;
import me.academeg.entity.Tag;
import me.academeg.exceptions.AccountPermissionException;
import me.academeg.exceptions.EmptyFieldException;
import me.academeg.exceptions.TagExistException;
import me.academeg.exceptions.TagNotExistException;
import me.academeg.security.Role;
import me.academeg.service.AccountService;
import me.academeg.service.TagService;
import me.academeg.utils.ApiUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.User;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

import static me.academeg.utils.ApiUtils.listResult;
import static me.academeg.utils.ApiUtils.singleResult;

/**
 * TagController Controller
 *
 * @author Yuriy A. Samsonov <y.samsonov@erpscan.com>
 * @version 1.0
 */
@RestController
@RequestMapping("/api/tag")
@Validated
public class TagController {

    private final TagService tagService;
    private final AccountService accountService;

    @Autowired
    public TagController(TagService tagService, AccountService accountService) {
        this.tagService = tagService;
        this.accountService = accountService;
    }

    @RequestMapping(value = "", method = RequestMethod.GET)
    public ApiResult getList(final Integer page, final Integer limit) {
        return listResult(tagService.getPerPage(ApiUtils.createPageRequest(limit, page, null)));
    }

    @RequestMapping(value = "", method = RequestMethod.POST)
    public ApiResult create(@RequestBody final Tag tag) {
        if (tag.getValue() == null || tag.getValue().isEmpty()) {
            throw new EmptyFieldException();
        }

        Tag tagFromDb = tagService.getByValue(tag.getValue());
        if (tagFromDb != null) {
            return singleResult(tagFromDb);
        }

        Tag dbTag = new Tag();
        dbTag.setValue(tag.getValue());
        return singleResult(tagService.add(dbTag));
    }

    @RequestMapping(value = "/{uuid}", method = RequestMethod.PUT)
    public ApiResult update(
            @AuthenticationPrincipal final User user,
            @PathVariable final UUID uuid,
            @RequestBody final Tag tag
    ) {
        Account authAccount = accountService.getByEmail(user.getUsername());
        if (!authAccount.getAuthority().equals(Role.ROLE_MODERATOR.name())
                && !authAccount.getAuthority().equals(Role.ROLE_ADMIN.name())) {
            throw new AccountPermissionException();
        }

        Tag tagFromDbUuid = tagService.getByUuid(uuid);
        if (tagService.getByUuid(uuid) == null) {
            throw new TagNotExistException();
        }

        Tag tagFromDbValue = tagService.getByValue(tag.getValue());
        if (tagFromDbValue != null && !tagFromDbValue.getId().equals(uuid)) {
            throw new TagExistException();
        }

        tagFromDbUuid.setValue(tag.getValue());
        return singleResult(tagService.edit(tagFromDbUuid));
    }

    @RequestMapping(value = "/{uuid}", method = RequestMethod.DELETE)
    @ResponseStatus(value = HttpStatus.NO_CONTENT)
    public void delete(@AuthenticationPrincipal final User user, final @PathVariable UUID uuid) {
        Account authAccount = accountService.getByEmail(user.getUsername());
        if (!authAccount.getAuthority().equals(Role.ROLE_MODERATOR.name())
                && !authAccount.getAuthority().equals(Role.ROLE_ADMIN.name())) {
            throw new AccountPermissionException();
        }

        Tag tagFromDb = tagService.getByUuid(uuid);
        if (tagFromDb == null) {
            throw new TagNotExistException();
        }

        tagFromDb.setArticles(null);
        tagService.edit(tagFromDb);
        tagService.delete(tagFromDb.getId());
    }
}
