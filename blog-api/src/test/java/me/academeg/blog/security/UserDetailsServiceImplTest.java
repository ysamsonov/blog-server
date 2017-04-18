package me.academeg.blog.security;

import me.academeg.blog.BaseTest;
import me.academeg.blog.dal.domain.Account;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author Yuriy A. Samsonov <y.samsonov@erpscan.com>
 * @since 17.04.2017
 */
@Transactional
@Rollback
public class UserDetailsServiceImplTest extends BaseTest {

    @Autowired
    private UserDetailsServiceImpl userDetailsService;

    @PersistenceContext
    private EntityManager entityManager;

    private UUID accId1;
    private UUID accId2;

    @Before
    public void setUp() throws Exception {
        Account acc1 = new Account(accId1);
        acc1.setName("Yura");
        acc1.setSurname("Samsonov");
        acc1.setLogin("academeg");
        acc1.setEmail("academeg@gmail.com");
        acc1.setPassword("1234");

        Account acc2 = new Account(accId2);
        acc2.setName("Yurets");
        acc2.setSurname("Samsonov");
        acc2.setLogin("yura");
        acc2.setEmail("yura@gmail.com");
        acc2.setPassword("1234");

        acc1 = entityManager.merge(acc1);
        accId1 = acc1.getId();

        acc2 = entityManager.merge(acc2);
        accId2 = acc2.getId();

        entityManager.flush();
        entityManager.clear();
    }

    @Test
    public void findAccountByLogin() throws Exception {
        UserDetailsImpl acc = userDetailsService.loadUserByUsername("academeg");
        assertThat(acc.getId()).isEqualTo(accId1);
    }

    @Test
    public void findAccountByEmail() throws Exception {
        UserDetailsImpl acc = userDetailsService.loadUserByUsername("yura@gmail.com");
        assertThat(acc.getId()).isEqualTo(accId2);
    }

    @Test(expected = UsernameNotFoundException.class)
    public void findNonExistAccount() throws Exception {
        userDetailsService.loadUserByUsername("nonExistCredential");
    }
}
