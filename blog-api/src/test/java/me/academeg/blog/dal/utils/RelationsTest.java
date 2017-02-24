package me.academeg.blog.dal.utils;

import me.academeg.blog.dal.utils.helperentities.Account;
import me.academeg.blog.dal.utils.helperentities.Book;
import me.academeg.blog.dal.utils.helperentities.Page;
import me.academeg.blog.dal.utils.helperentities.Role;
import org.junit.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author Yuriy A. Samsonov <y.samsonov@erpscan.com>
 * @date 23.02.2017
 */
public class RelationsTest {

    @Test
    public void setOneToManyTest() throws Exception {
        Book book = new Book("Thinking in Java");
        List<Page> pages = Arrays.asList(new Page(1), new Page(2));
        book.setPages(pages);

        assertThat(book.getPages())
            .extracting(Page::getNumber)
            .contains(1, 2);

        assertThat(pages.get(0).getBook().getName()).isEqualTo("Thinking in Java");
        assertThat(pages.get(1).getBook().getName()).isEqualTo("Thinking in Java");
    }

    @Test
    public void setOneToManyTwiceTest() throws Exception {
        Book book = new Book("Thinking in Java");
        List<Page> pages = Arrays.asList(new Page(1), new Page(2));
        book.setPages(pages);

        book.setPages(Collections.emptyList());

        assertThat(book.getPages().size()).isEqualTo(0);
        assertThat(pages.get(0).getBook()).isNull();
        assertThat(pages.get(1).getBook()).isNull();
    }

    @Test
    public void addOneToManyTest() throws Exception {
        Book book = new Book("Thinking in Java");
        Page page1 = new Page(1);
        Page page2 = new Page(2);

        book.addPage(page1);
        book.addPage(page2);

        assertThat(book.getPages())
            .extracting(Page::getNumber)
            .contains(1, 2);

        assertThat(page1.getBook().getName()).isEqualTo("Thinking in Java");
        assertThat(page2.getBook().getName()).isEqualTo("Thinking in Java");
    }

    @Test
    public void removeOneToManyTest() throws Exception {
        Book book = new Book("Thinking in Java");
        Page page1 = new Page(1);
        Page page2 = new Page(2);

        book.addPage(page1);
        book.addPage(page2);

        book.removePage(page1);
        assertThat(page1.getBook()).isNull();
        assertThat(book.getPages())
            .extracting(Page::getNumber)
            .contains(2);
    }

    @Test
    public void setManyToOneTest() throws Exception {
        Book book = new Book("Thinking in Java");
        Page page1 = new Page(1);
        Page page2 = new Page(2);

        page1.setBook(book);
        page2.setBook(book);

        assertThat(book.getPages())
            .extracting(Page::getNumber)
            .contains(1, 2);

        assertThat(page1.getBook().getName()).isEqualTo("Thinking in Java");
        assertThat(page2.getBook().getName()).isEqualTo("Thinking in Java");
    }

    @Test
    public void addManyToManyTest() throws Exception {
        Account account = new Account("Yuriy");
        Role role1 = new Role("role 1");
        Role role2 = new Role("role 2");

        account.addRole(role1);
        role2.addAccount(account);

        assertThat(account.getRoles())
            .extracting(Role::getName)
            .contains(
                "role 1",
                "role 2"
            );

        assertThat(role1.getAccounts())
            .extracting(Account::getName)
            .containsExactly("Yuriy");

        assertThat(role2.getAccounts())
            .extracting(Account::getName)
            .containsExactly("Yuriy");
    }

    @Test
    public void removeManyToManyTest() throws Exception {
        Account account = new Account("Yuriy");
        Role role1 = new Role("role 1");
        Role role2 = new Role("role 2");
        account.addRole(role1);
        role2.addAccount(account);

        account.removeRole(role1);

        assertThat(account.getRoles())
            .extracting(Role::getName)
            .contains("role 2");

        assertThat(role1.getAccounts().size()).isEqualTo(0);

        assertThat(role2.getAccounts())
            .extracting(Account::getName)
            .containsExactly("Yuriy");
    }

    @Test
    public void setManyToManyTest() throws Exception {
        Account account = new Account("Yuriy");
        List<Role> roles = Arrays.asList(
            new Role("role 1"),
            new Role("role 2")
        );

        account.setRoles(roles);

        assertThat(account.getRoles())
            .extracting(Role::getName)
            .contains(
                "role 1",
                "role 2"
            );

        assertThat(roles.get(0).getAccounts())
            .extracting(Account::getName)
            .containsExactly("Yuriy");

        assertThat(roles.get(1).getAccounts())
            .extracting(Account::getName)
            .containsExactly("Yuriy");
    }

    @Test
    public void setManyToManyTwiceTest() throws Exception {
        Account account = new Account("Yuriy");
        List<Role> roles = Arrays.asList(
            new Role("role 1"),
            new Role("role 2")
        );
        account.setRoles(roles);
        account.setRoles(Collections.emptyList());

        assertThat(account.getRoles().size()).isEqualTo(0);
        assertThat(roles.get(0).getAccounts().size()).isEqualTo(0);
        assertThat(roles.get(1).getAccounts().size()).isEqualTo(0);
    }
}
