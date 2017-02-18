package me.academeg.blog.security.config;

import me.academeg.blog.security.CorsFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurerAdapter;
import org.springframework.security.web.access.channel.ChannelProcessingFilter;

/**
 * Configure access to resources on server like accounts, comments and etc.
 *
 * @author Yuriy A. Samsonov <y.samsonov@erpscan.com>
 * @version 1.0
 * @date 18.02.2017
 */
@Configuration
@EnableResourceServer
public class ResourceServerConfiguration extends ResourceServerConfigurerAdapter {

    private final CorsFilter corsFilter;

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
            .and().httpBasic();

        // @TODO remove on production
        http.addFilterBefore(corsFilter, ChannelProcessingFilter.class);
    }
}
