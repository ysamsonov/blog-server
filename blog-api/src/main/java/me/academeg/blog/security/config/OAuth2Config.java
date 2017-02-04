package me.academeg.blog.security.config;

import me.academeg.blog.security.CorsFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.token.TokenEnhancer;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.oauth2.provider.token.store.JdbcTokenStore;
import org.springframework.security.web.access.channel.ChannelProcessingFilter;

import javax.sql.DataSource;

/**
 * OAuth2Config Configuration
 *
 * @author Yuriy A. Samsonov <yuriy.samsonov96@gmail.com>
 * @version 1.0
 */
@Configuration
public class OAuth2Config {

    @Configuration
    @EnableResourceServer
    protected static class ResourceServerConfiguration extends ResourceServerConfigurerAdapter {

        private CorsFilter corsFilter;

        @Autowired
        public ResourceServerConfiguration(CorsFilter corsFilter) {
            this.corsFilter = corsFilter;
        }

        @Override
        public void configure(HttpSecurity http) throws Exception {
            http
                .exceptionHandling()
                .and()
                .authorizeRequests()
                .antMatchers(HttpMethod.GET, "/").permitAll()
                .antMatchers(HttpMethod.GET, "/api/accounts/**").permitAll()
                .antMatchers(HttpMethod.POST, "/api/accounts").permitAll()
                .antMatchers(HttpMethod.GET, "/api/articles/*").permitAll()
                .antMatchers(HttpMethod.GET, "/api/articles/list").permitAll()
                .antMatchers(HttpMethod.GET, "/api/comments/**").permitAll()
                .antMatchers(HttpMethod.GET, "/api/tags/**").permitAll()
                .antMatchers(HttpMethod.GET, "/api/avatars/**").permitAll()
                .antMatchers(HttpMethod.GET, "/api/images/**").permitAll()
                .anyRequest().authenticated()
                .and().httpBasic()
                .and().addFilterBefore(corsFilter, ChannelProcessingFilter.class); //@TODO remove on production
        }
    }

    @Configuration
    @EnableAuthorizationServer
    protected static class AuthorizationServerConfiguration extends AuthorizationServerConfigurerAdapter {

        private AuthenticationManager authenticationManager;
        private DataSource dataSource;
        private TokenEnhancer tokenEnhancer;
        private UserDetailsService userDetailsService;

        @Autowired
        public AuthorizationServerConfiguration(
            @Qualifier("authenticationManagerBean") AuthenticationManager authenticationManager,
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
        public void configure(AuthorizationServerSecurityConfigurer oauthServer) throws Exception {
            oauthServer
                .tokenKeyAccess("permitAll()")
                .checkTokenAccess("isAuthenticated()");
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
}
