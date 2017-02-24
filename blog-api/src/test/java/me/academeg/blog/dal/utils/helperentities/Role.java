package me.academeg.blog.dal.utils.helperentities;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import me.academeg.blog.dal.domain.BaseEntity;

import java.util.HashSet;
import java.util.Set;

import static me.academeg.blog.dal.utils.Relations.addManyToMany;
import static me.academeg.blog.dal.utils.Relations.removeManyToMany;

/**
 * @author Yuriy A. Samsonov <y.samsonov@erpscan.com>
 * @date 23.02.2017
 */
@Getter
@Setter
@Accessors(chain = true)
public class Role extends BaseEntity {

    private String name;

    private Set<Account> accounts = new HashSet<>();

    public Role(String name) {
        this.name = name;
    }

    @SuppressWarnings("WeakerAccess")
    public boolean hasAccount(Account account) {
        return this.accounts.contains(account);
    }

    public Role addAccount(Account account) {
        return addManyToMany(
            this,
            account,
            Role::hasAccount,
            accounts::add,
            Account::addRole
        );
    }

    @SuppressWarnings("WeakerAccess")
    public Role removeAccount(Account account) {
        return removeManyToMany(
            this,
            account,
            Role::hasAccount,
            accounts::remove,
            Account::removeRole
        );
    }

//    public Collection<Account> getAccounts() {
//        return getOneToMany(this.accounts);
//    }
//
//    public void setAccounts(Set<Account> accounts) {
//        setManyToMany(
//            accounts,
//            this.accounts,
//            this::removeAccount,
//            this::addAccount
//        );
//    }
}
