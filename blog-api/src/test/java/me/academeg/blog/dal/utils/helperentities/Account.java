package me.academeg.blog.dal.utils.helperentities;

import lombok.Getter;
import lombok.Setter;
import me.academeg.blog.dal.domain.BaseEntity;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import static me.academeg.blog.dal.utils.Relations.*;

/**
 * @author Yuriy A. Samsonov <y.samsonov@erpscan.com>
 * @date 23.02.2017
 */
public class Account extends BaseEntity {

    @Getter
    @Setter
    private String name;

    private Set<Role> roles = new HashSet<>();

    public Account(String name) {
        this.name = name;
    }

    public Account addRole(Role role) {
        return addManyToMany(
            this,
            role,
            Account::hasRole,
            roles::add,
            Role::addAccount
        );
    }

    public Account removeRole(Role role) {
        return removeManyToMany(
            this,
            role,
            Account::hasRole,
            roles::remove,
            Role::removeAccount
        );
    }

    public boolean hasRole(Role role) {
        return this.roles.contains(role);
    }

    public Collection<Role> getRoles() {
        return getOneToMany(this.roles);
    }

    public void setRoles(Collection<Role> roles) {
        setManyToMany(
            roles,
            this.roles,
            Collection::remove,
            this::addRole
        );
    }
}
