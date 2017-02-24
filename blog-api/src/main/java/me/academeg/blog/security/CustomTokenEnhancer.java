package me.academeg.blog.security;

import me.academeg.blog.dal.domain.Account;
import me.academeg.blog.dal.repository.AccountRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.oauth2.common.DefaultOAuth2AccessToken;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.token.TokenEnhancer;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * CustomTokenEnhancer
 *
 * @author Yuriy A. Samsonov <y.samsonov@erpscan.com>
 * @version 1.0
 */
@Component
public class CustomTokenEnhancer implements TokenEnhancer {

    private final AccountRepository repository;

    @Autowired
    public CustomTokenEnhancer(AccountRepository repository) {
        this.repository = repository;
    }

    @Override
    public OAuth2AccessToken enhance(OAuth2AccessToken accessToken, OAuth2Authentication authentication) {
        final Map<String, Object> additionalInfo = new HashMap<>();
        // TODO: 24.02.2017 create custom UserDetailsIml that will contain accountId
        Account account = repository.getByEmailIgnoreCase(((User) authentication.getPrincipal()).getUsername());
        additionalInfo.put("account_id", account.getId());
        ((DefaultOAuth2AccessToken) accessToken).setAdditionalInformation(additionalInfo);
        return accessToken;
    }
}
