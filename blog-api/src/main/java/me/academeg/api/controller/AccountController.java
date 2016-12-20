package me.academeg.api.controller;

import me.academeg.api.common.ApiResult;
import me.academeg.api.entity.Account;
import me.academeg.api.entity.AccountRole;
import me.academeg.api.exception.entity.AccountNotExistException;
import me.academeg.api.exception.entity.AccountPermissionException;
import me.academeg.api.exception.entity.EmailExistException;
import me.academeg.api.exception.entity.LoginExistException;
import me.academeg.api.service.AccountService;
import me.academeg.api.utils.ApiUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.validation.Validation;
import javax.validation.Validator;
import java.util.Collection;
import java.util.Set;
import java.util.UUID;

import static me.academeg.api.utils.ApiUtils.listResult;
import static me.academeg.api.utils.ApiUtils.singleResult;

/**
 * AccountController Controller
 *
 * @author Yuriy A. Samsonov <yuriy.samsonov96@gmail.com>
 * @version 1.0
 */
@RestController
@RequestMapping("/api/account")
public class AccountController {

    private final PasswordEncoder passwordEncoder;
    private final AccountService accountService;
    private final TokenStore tokenStore;

    private final Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

    @Autowired
    public AccountController(
        PasswordEncoder passwordEncoder,
        AccountService accountService,
        TokenStore tokenStore
    ) {
        this.passwordEncoder = passwordEncoder;
        this.accountService = accountService;
        this.tokenStore = tokenStore;
    }

    @RequestMapping(value = "", method = RequestMethod.POST)
    public ApiResult create(@Validated @RequestBody final Account acc) {
        if (accountService.getByEmail(acc.getEmail()) != null) {
            throw new EmailExistException("Email is already exist");
        }
        if (accountService.getByLogin(acc.getLogin()) != null) {
            throw new LoginExistException("Login is already exist");
        }

        Account accountDb = new Account();
        accountDb.setLogin(acc.getLogin());
        accountDb.setName(acc.getName());
        accountDb.setSurname(acc.getSurname());
        accountDb.setEmail(acc.getEmail());
        accountDb.setPassword(passwordEncoder.encode(acc.getPassword()));
        accountDb.setAuthority(AccountRole.USER);
        return singleResult(accountService.add(accountDb));
    }

    @RequestMapping(value = "/{uuid}", method = RequestMethod.PUT)
    public ApiResult update(
        @AuthenticationPrincipal final User user,
        @PathVariable final UUID uuid,
        @RequestBody final Account acc
    ) {
        Set<ConstraintViolation<Account>> validated = validator.validateProperty(acc, "login");
        if (validated.size() > 0) {
            throw new ConstraintViolationException(validated);
        }

        Account authUser = accountService.getByEmail(user.getUsername());
        if (!authUser.getId().equals(uuid)) {
            throw new AccountNotExistException();
        }
        if (!authUser.getLogin().equals(acc.getLogin()) && accountService.getByLogin(acc.getLogin()) != null) {
            throw new LoginExistException();
        }

        authUser.setSurname(acc.getSurname());
        authUser.setName(acc.getName());
        authUser.setLogin(acc.getLogin());
        return singleResult(accountService.add(authUser));
    }

    @RequestMapping(value = "/{uuid}", method = RequestMethod.GET)
    public ApiResult getById(@PathVariable final UUID uuid) {
        Account account = accountService.getById(uuid);
        if (account == null) {
            throw new AccountNotExistException();
        }
        return singleResult(account);
    }

    @RequestMapping(value = "/list", method = RequestMethod.GET)
    public ApiResult getList(final Integer page, final Integer limit) {
        return listResult(accountService.getAll(ApiUtils.createPageRequest(limit, page, null)));
    }

    @RequestMapping(value = "/{uuid}", method = RequestMethod.DELETE)
    @ResponseStatus(value = HttpStatus.NO_CONTENT)
    public void delete(
        @PathVariable final UUID uuid,
        @AuthenticationPrincipal final User user
    ) {
        //@TODO обсудить удаление пользователя, а именно его контент
        Account deletedUser = accountService.getById(uuid);
        if (deletedUser == null) {
            throw new AccountNotExistException();
        }

        Account authUser = accountService.getByEmail(user.getUsername());
        if (!authUser.getId().equals(deletedUser.getId()) && !authUser.getAuthority().equals(AccountRole.ADMIN)) {
            throw new AccountPermissionException("You have not permission");
        }
//        removeTokens(deletedUser);
//        accountService.delete(deletedUser);
    }

    private void removeTokens(final Account account) {
        Collection<OAuth2AccessToken> tokens
            = tokenStore.findTokensByClientIdAndUserName("web_app", account.getEmail());

        for (OAuth2AccessToken token : tokens) {
            tokenStore.removeAccessToken(token);
        }
    }
}
