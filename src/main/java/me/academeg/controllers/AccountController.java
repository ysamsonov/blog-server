package me.academeg.controllers;

import me.academeg.entity.Account;
import me.academeg.security.Role;
import me.academeg.service.AccountService;
import org.springframework.beans.factory.annotation.Autowired;
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
        System.out.println(acc);
        if (acc.getEmail() == null || acc.getLogin() == null || acc.getPassword() == null) {
            throw new IllegalArgumentException("Email, login and password cannot be null");
        }
        if (accountService.getByEmail(acc.getEmail()) != null) {
            throw new IllegalArgumentException("Email is already used");
        }
        if (accountService.getByLogin(acc.getLogin()) != null) {
            throw new IllegalArgumentException("Login is already used");
        }

        acc.setPassword(passwordEncoder.encode(acc.getPassword()));
        acc.setAuthority(Role.ROLE_USER.name());
        return accountService.add(acc);
    }

    @RequestMapping(value = "", method = RequestMethod.PUT)
    public Account updateAccount(@Valid @RequestBody Account acc,
                                 @AuthenticationPrincipal User user) {
        if (acc.getEmail() == null || acc.getLogin() == null) {
            throw new IllegalArgumentException("Email and login cannot be null");
        }

        Account authUser = accountService.getByEmail(user.getUsername());
        if (acc.getEmail() != null && !authUser.getEmail().equals(acc.getEmail())) {
            if (accountService.getByEmail(acc.getEmail()) != null) {
                throw new IllegalArgumentException("Email is already used");
            }
        }
        if (acc.getLogin() != null && !authUser.getLogin().equals(acc.getLogin())) {
            if (accountService.getByLogin(acc.getLogin()) != null) {
                throw new IllegalArgumentException("Login is already used");
            }
        }

        authUser.setSurname(acc.getSurname());
        authUser.setName(acc.getName());
        authUser.setEmail(acc.getEmail());
        authUser.setLogin(acc.getLogin());
        return accountService.add(authUser);
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public Account getAccount(@PathVariable UUID id) {
        System.out.println(id);
        Account byId = accountService.getById(id);
        if (byId == null) {
            throw new IllegalArgumentException("Account not exist");
        }
        return byId;
    }

    @RequestMapping(value = "/all", method = RequestMethod.GET)
    public Iterable<Account> getAllAccounts() {
        return accountService.getAll();
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    public String deleteAccount(@PathVariable UUID id,
                                @AuthenticationPrincipal User user,
                                HttpServletRequest request) {

        Account authUser = accountService.getByEmail(user.getUsername());
        Account userById = accountService.getById(id);

        if (!(authUser.getId() == userById.getId() || authUser.getAuthority().equals(Role.ROLE_ADMIN.name()))) {
            throw new IllegalArgumentException("You have not access");
        }
        accountService.delete(userById);
        removeToken(request);
        return "OK";
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
