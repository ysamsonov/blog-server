package me.academeg.blog.security.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer;
import org.springframework.security.oauth2.provider.token.TokenEnhancer;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.oauth2.provider.token.store.JdbcTokenStore;

import javax.sql.DataSource;

/**
 * Authorization server is the one responsible for verifying credentials
 * and if credentials are OK, providing the tokens
 *
 * @author Yuriy A. Samsonov <y.samsonov@erpscan.com>
 * @version 1.0
 * @date 18.02.2017
 */
@Configuration
@EnableAuthorizationServer
public class AuthorizationServerConfiguration extends AuthorizationServerConfigurerAdapter {

    private final AuthenticationManager authenticationManager;
    private final DataSource dataSource;
    private final TokenEnhancer tokenEnhancer;
    private final UserDetailsService userDetailsService;

    @Autowired
    public AuthorizationServerConfiguration(
        AuthenticationManager authenticationManager,
        DataSource dataSource,
        TokenEnhancer tokenEnhancer,
        UserDetailsService userDetailsService
    ) {
        this.authenticationManager = authenticationManager;
        this.dataSource = dataSource;
        this.tokenEnhancer = tokenEnhancer;
        this.userDetailsService = userDetailsService;
    }

    @Override
    public void configure(ClientDetailsServiceConfigurer clients) throws Exception {
        clients
            .inMemory()
            .withClient("web_app")
            .secret("secret_key")
            .authorizedGrantTypes("password", "refresh_token")
            .scopes("read", "write")
            .autoApprove(true);
    }

    @Override
    public void configure(AuthorizationServerEndpointsConfigurer endpoints) throws Exception {
        endpoints
            .tokenStore(tokenStoreBean())
            .tokenEnhancer(tokenEnhancer)
            .authenticationManager(authenticationManager)
            .userDetailsService(userDetailsService);
    }

    @Bean
    public TokenStore tokenStoreBean() {
        return new JdbcTokenStore(dataSource);
    }
}
