package me.academeg.api.rest;

import me.academeg.common.ApiResult;
import me.academeg.entity.Account;
import me.academeg.exceptions.*;
import me.academeg.security.Role;
import me.academeg.service.AccountService;
import me.academeg.utils.ApiUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Collection;
import java.util.UUID;

import static me.academeg.utils.ApiUtils.listResult;
import static me.academeg.utils.ApiUtils.singleResult;

/**
 * AccountController Controller
 *
 * @author Yuriy A. Samsonov <yuriy.samsonov96@gmail.com>
 * @version 1.0
 */
@RestController
@RequestMapping("/api/account")
@Validated
public class AccountController {

    private final PasswordEncoder passwordEncoder;
    private final AccountService accountService;
    private final TokenStore tokenStore;

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
    public ApiResult create(@Valid @RequestBody final Account acc) {
        if (acc.getEmail() == null || acc.getLogin() == null || acc.getPassword() == null) {
            throw new EmptyFieldException("Email, login and password cannot be null");
        }
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
        accountDb.setAuthority(Role.ROLE_USER.name());
        return singleResult(accountService.add(accountDb));
    }

    @RequestMapping(value = "/{uuid}", method = RequestMethod.PUT)
    public ApiResult update(
            @AuthenticationPrincipal final User user,
            @PathVariable final UUID uuid,
            @Valid @RequestBody final Account acc
    ) {
        if (acc.getLogin() == null) {
            throw new EmptyFieldException("Login cannot be null");
        }

        Account authUser = accountService.getByEmail(user.getUsername());
        if (!authUser.getId().equals(uuid)) {
            throw new AccountNotExistException("Account not exist");
        }
        if (!authUser.getLogin().equals(acc.getLogin()) && accountService.getByLogin(acc.getLogin()) != null) {
            throw new LoginExistException("Login is already used");
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
            throw new AccountNotExistException("Account not exist");
        }
        return singleResult(account);
    }

    @RequestMapping(value = "", method = RequestMethod.GET)
    public ApiResult getList(final Integer page, final Integer limit) {
        return listResult(accountService.getAll(ApiUtils.createPageRequest(limit, page, null)));
    }

    @RequestMapping(value = "/{uuid}", method = RequestMethod.DELETE)
    @ResponseStatus(value = HttpStatus.NO_CONTENT)
    public void delete(
            @PathVariable final UUID uuid,
            @AuthenticationPrincipal final User user
    ) {
        Account deletedUser = accountService.getById(uuid);
        if (deletedUser == null) {
            throw new AccountNotExistException("Account not exist");
        }

        Account authUser = accountService.getByEmail(user.getUsername());
        if (!authUser.getId().equals(deletedUser.getId()) && !authUser.getAuthority().equals(Role.ROLE_ADMIN.name())) {
            throw new AccountPermissionException("You have not permission");
        }
        removeTokens(deletedUser);
        accountService.delete(deletedUser);
    }

    private void removeTokens(final Account account) {
        Collection<OAuth2AccessToken> tokens
                = tokenStore.findTokensByClientIdAndUserName("web_app", account.getEmail());

        for (OAuth2AccessToken token : tokens) {
            tokenStore.removeAccessToken(token);
        }
    }
}
