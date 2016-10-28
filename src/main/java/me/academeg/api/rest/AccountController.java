package me.academeg.api.rest;

import me.academeg.entity.Account;
import me.academeg.exceptions.*;
import me.academeg.security.Role;
import me.academeg.service.AccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.UUID;

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

    private PasswordEncoder passwordEncoder;
    private AccountService accountService;
    private TokenStore tokenStore;

    @Autowired
    public AccountController(PasswordEncoder passwordEncoder, AccountService accountService, TokenStore tokenStore) {
        this.passwordEncoder = passwordEncoder;
        this.accountService = accountService;
        this.tokenStore = tokenStore;
    }

    @RequestMapping(value = "", method = RequestMethod.POST)
    public Account createAccount(@Valid @RequestBody Account acc) {
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
        return accountService.add(accountDb);
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.PUT)
    public Account updateAccount(@AuthenticationPrincipal User user, @PathVariable UUID id,
                                 @Valid @RequestBody Account acc) {

        if (acc.getLogin() == null) {
            throw new EmptyFieldException("Login cannot be null");
        }

        Account authUser = accountService.getByEmail(user.getUsername());
        if (!authUser.getId().equals(id)) {
            throw new AccountNotExistException("Account not exist");
        }
        if (!authUser.getLogin().equals(acc.getLogin()) && accountService.getByLogin(acc.getLogin()) != null) {
            throw new LoginExistException("Login is already used");
        }

        authUser.setSurname(acc.getSurname());
        authUser.setName(acc.getName());
        authUser.setLogin(acc.getLogin());
        return accountService.add(authUser);
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public Account getAccount(@PathVariable UUID id) {
        Account account = accountService.getById(id);
        if (account == null) {
            throw new AccountNotExistException("Account not exist");
        }
        return account;
    }

    @RequestMapping(value = "", method = RequestMethod.GET)
    public Page<Account> getAllAccounts(@RequestParam(defaultValue = "0") int page,
                                        @RequestParam(defaultValue = "20") int size) {
        PageRequest pageRequest = new PageRequest(page, size);
        return accountService.getAll(pageRequest);
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    @ResponseStatus(value = HttpStatus.NO_CONTENT)
    public void deleteAccount(@PathVariable UUID id, @AuthenticationPrincipal User user, HttpServletRequest request) {
        Account deletedUser = accountService.getById(id);
        if (deletedUser == null) {
            throw new AccountNotExistException("Account not exist");
        }

        Account authUser = accountService.getByEmail(user.getUsername());
        if (!authUser.getId().equals(deletedUser.getId()) && !authUser.getAuthority().equals(Role.ROLE_ADMIN.name())) {
            throw new AccountPermissionException("You have not permission");
        }
        if (deletedUser.getId().equals(authUser.getId())) {
            removeToken(request);
        }
        accountService.delete(deletedUser);
    }

    private void removeToken(HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");
        if (authHeader != null) {
            String tokenValue = authHeader.replace("Bearer", "").trim();
            OAuth2AccessToken accessToken = tokenStore.readAccessToken(tokenValue);
            tokenStore.removeAccessToken(accessToken);
        }
    }
}
