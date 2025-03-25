package com.homework.task.database.templates;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.Collection;
import java.util.Collections;

public class User {
    private long id;
    private String username;
    private String password;
    private String salt;
    private Role role;

    public enum Role {
        USER,
        ADMIN,
        SUPERADMIN
    }

    public User() {
    }

    public User(long id, String username, String password, String salt, Role role) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.salt = salt;
        this.role = role;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getSalt() {
        return salt;
    }

    public void setSalt(String salt) {
        this.salt = salt;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }
    public Collection<? extends GrantedAuthority> getAuthorities() {
        // Convert the role string into a GrantedAuthority object
        return Collections.singletonList(new SimpleGrantedAuthority(role.name()));
    }
}
