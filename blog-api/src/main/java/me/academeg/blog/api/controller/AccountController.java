package me.academeg.blog.api.controller;

import lombok.extern.slf4j.Slf4j;
import me.academeg.blog.api.common.ApiResult;
import me.academeg.blog.dal.domain.Account;
import me.academeg.blog.dal.service.AccountService;
import me.academeg.blog.security.RoleConstants;
import me.academeg.blog.security.UserDetailsImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

import static me.academeg.blog.api.utils.ApiUtils.*;

/**
 * AccountController Controller
 *
 * @author Yuriy A. Samsonov <yuriy.samsonov96@gmail.com>
 * @version 1.0
 */
@RestController
@RequestMapping("/api/accounts")
@Slf4j
public class AccountController {

    private final AccountService accountService;
    private final Class resourceClass;

    @Autowired
    public AccountController(final AccountService accountService) {
        this.accountService = accountService;
        this.resourceClass = Account.class;
    }

    @RequestMapping(value = "", method = RequestMethod.POST)
    public ApiResult create(@Validated @RequestBody final Account account) {
        log.info("/CREATE method invoked for {}", resourceClass.getSimpleName());
        return singleResult(accountService.create(account));
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.PUT)
    public ApiResult update(
        @AuthenticationPrincipal final UserDetailsImpl user,
        @PathVariable final UUID id,
        @RequestBody final Account account
    ) {
        log.info("/UPDATE method invoked for {} id {}", resourceClass.getSimpleName(), id);
        if (!user.getId().equals(id)) {
            throw new AccessDeniedException("You don't have rights to update account");
        }
        account.setId(id);
        return singleResult(accountService.update(account));
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public ApiResult getById(@PathVariable final UUID id) {
        log.info("/GET method invoked for {} id {}", resourceClass.getSimpleName(), id);
        return singleResult(accountService.getById(id));
    }

    @RequestMapping(value = "/list", method = RequestMethod.GET)
    public ApiResult getList(
        @RequestParam(required = false) final Integer page,
        @RequestParam(required = false) final Integer limit,
        @RequestParam(required = false) final String orderBy
    ) {
        log.info("/LIST method invoked for {}", resourceClass.getSimpleName());
        return listResult(accountService.getPage(createPageRequest(limit, page, orderBy)));
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    public ApiResult delete(
        @PathVariable final UUID id,
        @AuthenticationPrincipal final UserDetailsImpl user
    ) {
        log.info("/DELETE method invoked for {} id {}", resourceClass.getSimpleName(), id);
        if (!user.getId().equals(id) && !user.hasAuthority(RoleConstants.ADMIN)) {
            throw new AccessDeniedException(String.format("You don't have rights to delete account with id %s", id));
        }
        accountService.delete(id);
        return okResult();
    }
}
