package me.academeg.blog.dal.service;

import me.academeg.blog.api.exception.BlogEntityExistException;
import me.academeg.blog.api.exception.BlogEntityNotExistException;
import me.academeg.blog.dal.domain.Account;
import org.junit.Ignore;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author Yuriy A. Samsonov <y.samsonov@erpscan.com>
 * @since 16.04.2017
 */
public class AccountServiceTest extends BaseServiceTest {

    @Autowired
    private AccountService accountService;

    @Test
    public void create() throws Exception {
        Account expected = prepareEntity();

        Account savedAccount = accountService.create(prepareEntity());
        savedAccount = accountService.getById(savedAccount.getId());

        assertThat(savedAccount.getName()).isEqualTo(expected.getName());
        assertThat(savedAccount.getSurname()).isEqualTo(expected.getSurname());
        assertThat(savedAccount.getEmail()).isEqualTo(expected.getEmail());
        assertThat(savedAccount.getLogin()).isEqualTo(expected.getLogin());
        assertThat(savedAccount.getPassword()).isNotEqualTo(expected.getPassword());
    }

    @Test
    public void update() throws Exception {
        Account expected = modifyEntity(prepareEntity());

        Account savedAccount = accountService.create(prepareEntity());
        savedAccount = accountService.getById(savedAccount.getId());

        savedAccount = modifyEntity(savedAccount);
        savedAccount = accountService.update(savedAccount);
        savedAccount = accountService.getById(savedAccount.getId());

        assertThat(savedAccount.getName()).isEqualTo(expected.getName());
        assertThat(savedAccount.getSurname()).isEqualTo(expected.getSurname());
        assertThat(savedAccount.getEmail()).isEqualTo(expected.getEmail());
        assertThat(savedAccount.getLogin()).isEqualTo(expected.getLogin());
        assertThat(savedAccount.getPassword()).isNotEqualTo(expected.getPassword());
    }

    @Test(expected = BlogEntityNotExistException.class)
    public void deleteNotExistAccount() throws Exception {
        accountService.delete(UUID.randomUUID());
    }

    // TODO: 16.04.2017 improve test to delete avatar, remove link to articles and comments
    // TODO: 16.04.2017 разобраться почему не создается таблица для токена
    @Ignore
    @Test
    public void delete() throws Exception {
        Account savedAccount = accountService.create(prepareEntity());
        savedAccount = accountService.getById(savedAccount.getId());

        accountService.delete(savedAccount.getId());

        Account account = accountService.getById(savedAccount.getId());
        assertThat(account).isNull();
    }

    @Test(expected = BlogEntityExistException.class)
    public void createWithExistingLogin() throws Exception {
        accountService.create(prepareEntity());
        accountService.create(prepareEntity().setEmail("other@gmail.com"));
    }

    @Test(expected = BlogEntityExistException.class)
    public void createWithExistingEmail() throws Exception {
        accountService.create(prepareEntity());
        accountService.create(prepareEntity().setLogin("otherLogin"));
    }

    @Test
    public void getByLogin() throws Exception {
        List<Account> accounts = prepareListEntity();
        final String login = accounts.get(0).getLogin();
        accounts.forEach(accountService::create);

        Account account = accountService.getByLogin(login);
        assertThat(account.getLogin()).isEqualTo(login);
    }

    @Test
    public void getByEmail() throws Exception {
        List<Account> accounts = prepareListEntity();
        final String email = accounts.get(0).getEmail();
        accounts.forEach(accountService::create);

        Account account = accountService.getByEmail(email);
        assertThat(account.getEmail()).isEqualTo(email);
    }

    @Test
    public void block() throws Exception {
        prepareListEntity().forEach(accountService::create);

        List<Account> accounts = accountService.getPage(null).getContent();
        accounts
            .stream()
            .map(Account::getEnable)
            .forEach(el -> assertThat(el).isTrue());

        List<UUID> ids = accounts.stream().map(Account::getId).collect(Collectors.toList());
        Random random = new Random(System.currentTimeMillis());
        ids.remove(random.nextInt(ids.size()));
        ids.remove(random.nextInt(ids.size()));
        ids.remove(random.nextInt(ids.size()));
        accountService.block(ids);

        accounts = accountService.getPage(null).getContent();
        accounts
            .stream()
            .filter(el -> ids.contains(el.getId()))
            .map(Account::getEnable)
            .forEach(el -> assertThat(el).isFalse());

        accounts
            .stream()
            .filter(el -> !ids.contains(el.getId()))
            .map(Account::getEnable)
            .forEach(el -> assertThat(el).isTrue());
    }

    @Test
    public void unlock() throws Exception {
        prepareListEntity().forEach(accountService::create);

        List<Account> accounts = accountService.getPage(null).getContent();
        accounts
            .stream()
            .map(Account::getEnable)
            .forEach(el -> assertThat(el).isTrue());

        List<UUID> ids = accounts.stream().map(Account::getId).collect(Collectors.toList());
        Random random = new Random(System.currentTimeMillis());
        ids.remove(random.nextInt(ids.size()));
        ids.remove(random.nextInt(ids.size()));
        ids.remove(random.nextInt(ids.size()));
        accountService.block(ids);

        accountService.unlock(accounts.stream().map(Account::getId).collect(Collectors.toList()));
        accounts = accountService.getPage(null).getContent();
        accounts
            .stream()
            .map(Account::getEnable)
            .forEach(el -> assertThat(el).isTrue());
    }

    private Account prepareEntity() {
        Account account = new Account();
        account.setName("Yuriy");
        account.setSurname("Samsonov");
        account.setEmail("y.samsonov96@gmail.com");
        account.setLogin("Academeg");
        account.setPassword("1234");
        return account;
    }

    private List<Account> prepareListEntity() {
        ArrayList<Account> accounts = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            Account acc = prepareEntity();
            acc.setEmail(i + acc.getEmail());
            acc.setLogin(i + acc.getLogin());
            accounts.add(acc);
        }
        return accounts;
    }

    private Account modifyEntity(Account account) {
        account.setLogin("Ya academeg");
        account.setEmail("yy123456@gmail.com");
        account.setSurname("Sam");
        return account;
    }
}
