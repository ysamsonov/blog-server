package me.academeg.blog.security;

import com.tngtech.java.junit.dataprovider.DataProvider;
import com.tngtech.java.junit.dataprovider.DataProviderRunner;
import com.tngtech.java.junit.dataprovider.UseDataProvider;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.Collections;
import java.util.HashSet;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author Yuriy A. Samsonov <y.samsonov@erpscan.com>
 * @since 17.04.2017
 */
@RunWith(DataProviderRunner.class)
public class UserDetailsImplTest {

    @DataProvider
    public static Object[][] wrongCredentialsDataProvider() {
        return new Object[][]{
            {null, null, null},
            {null, "", null},
            {UUID.randomUUID(), null, null},
            {UUID.randomUUID(), "", null},
            {UUID.randomUUID(), "", ""},
            {null, "", ""},
        };
    }


    @Test(expected = IllegalArgumentException.class)
    @UseDataProvider("wrongCredentialsDataProvider")
    public void wrongCredentialsTest(UUID id, String username, String password) throws Exception {
        new UserDetailsImpl(id, username, password, Collections.emptyList());
    }

    @Test
    public void createUseDefaultFields() throws Exception {
        UUID id = UUID.randomUUID();
        UserDetailsImpl ud = new UserDetailsImpl(id, "academeg", "1234", Collections.emptyList());

        assertThat(ud.getId()).isEqualTo(id);
        assertThat(ud.getUsername()).isEqualTo("academeg");
        assertThat(ud.getPassword()).isEqualTo("1234");
        assertThat(ud.getAuthorities()).isEmpty();
        assertThat(ud.isEnabled()).isTrue();
        assertThat(ud.isAccountNonExpired()).isTrue();
        assertThat(ud.isAccountNonLocked()).isTrue();
        assertThat(ud.isCredentialsNonExpired()).isTrue();
    }

    @Test
    public void createUseCustomFields() throws Exception {
        UUID id = UUID.randomUUID();
        UserDetailsImpl ud = new UserDetailsImpl(
            id,
            "academeg",
            "1234",
            false,
            false,
            false,
            false,
            Collections.emptyList()
        );

        assertThat(ud.getId()).isEqualTo(id);
        assertThat(ud.getUsername()).isEqualTo("academeg");
        assertThat(ud.getPassword()).isEqualTo("1234");
        assertThat(ud.getAuthorities()).isEmpty();
        assertThat(ud.isEnabled()).isFalse();
        assertThat(ud.isAccountNonExpired()).isFalse();
        assertThat(ud.isAccountNonLocked()).isFalse();
        assertThat(ud.isCredentialsNonExpired()).isFalse();
    }

    @Test
    public void hasAuthorityString() throws Exception {
        HashSet<GrantedAuthority> authorities = new HashSet<>();
        authorities.add(new SimpleGrantedAuthority("admin"));
        UserDetailsImpl ud = new UserDetailsImpl(UUID.randomUUID(), "academeg", "1234", authorities);

        assertThat(ud.getAuthorities().size()).isEqualTo(1);
        assertThat(ud.hasAuthority("admin")).isTrue();
        assertThat(ud.hasAuthority(new SimpleGrantedAuthority("admin"))).isTrue();
    }

    @Test
    public void setPasswordTest() throws Exception {
        UserDetailsImpl ud = new UserDetailsImpl(UUID.randomUUID(), "academeg", "1234", Collections.emptyList());
        assertThat(ud.getPassword()).isEqualTo("1234");

        ud.setPassword("4321");
        assertThat(ud.getPassword()).isEqualTo("4321");
    }
}
