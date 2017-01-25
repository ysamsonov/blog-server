package me.academeg.blog.api.controller;

import lombok.extern.slf4j.Slf4j;
import me.academeg.blog.api.common.ApiResult;
import me.academeg.blog.api.exception.AccountPermissionException;
import me.academeg.blog.api.exception.EntityNotExistException;
import me.academeg.blog.dal.domain.Account;
import me.academeg.blog.dal.domain.AccountRole;
import me.academeg.blog.dal.service.AccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.validation.Validation;
import javax.validation.Validator;
import java.util.Optional;
import java.util.Set;
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
    private final TokenStore tokenStore;
    private final Class resourceClass;

    private final Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

    @Autowired
    public AccountController(
        AccountService accountService,
        TokenStore tokenStore
    ) {
        this.accountService = accountService;
        this.tokenStore = tokenStore;
        this.resourceClass = Account.class;
    }

    @RequestMapping(value = "", method = RequestMethod.POST)
    public ApiResult create(@Validated @RequestBody final Account account) {
        log.info("/CREATE method invoked for {}", resourceClass.getSimpleName());
        return singleResult(accountService.create(account));
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.PUT)
    public ApiResult update(
        @AuthenticationPrincipal final User user,
        @PathVariable final UUID id,
        @RequestBody final Account account
    ) {
        log.info("/UPDATE method invoked for {} id {}", resourceClass.getSimpleName(), id);
        Set<ConstraintViolation<Account>> validated = validator.validateProperty(account, "login");
        if (validated.size() > 0) {
            throw new ConstraintViolationException(validated);
        }

        Account authUser = accountService.getByEmail(user.getUsername());
        if (!authUser.getId().equals(id)) {
            throw new EntityNotExistException("Account with id %s not exist", id);
        }

        account.setId(id);
        return singleResult(accountService.update(account));
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public ApiResult getById(@PathVariable final UUID id) {
        log.info("/GET method invoked for {} id {}", resourceClass.getSimpleName(), id);
        return singleResult(
            Optional
                .ofNullable(accountService.getById(id))
                .<EntityNotExistException>orElseThrow(
                    () -> new EntityNotExistException("Account with id %s not exist", id)));
    }

    @RequestMapping(value = "/list", method = RequestMethod.GET)
    public ApiResult getList(final Integer page, final Integer limit) {
        log.info("/LIST method invoked for {}", resourceClass.getSimpleName());
        return listResult(accountService.getPage(createPageRequest(limit, page, null)));
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    public ApiResult delete(
        @PathVariable final UUID id,
        @AuthenticationPrincipal final User user
    ) {
        log.info("/DELETE method invoked for {} id {}", resourceClass.getSimpleName(), id);
        Account deletedUser = accountService.getById(id);
        if (deletedUser == null) {
            throw new EntityNotExistException("Account with id %s not exist", id);
        }

        Account authUser = accountService.getByEmail(user.getUsername());
        if (!authUser.getId().equals(deletedUser.getId()) && !authUser.getAuthority().equals(AccountRole.ADMIN)) {
            throw new AccountPermissionException("You have not permission");
        }
        removeTokens(deletedUser);
        accountService.delete(id);
        return okResult();
    }

    private void removeTokens(final Account account) {
        tokenStore
            .findTokensByClientIdAndUserName("web_app", account.getEmail())
            .forEach(tokenStore::removeAccessToken);
    }
}
