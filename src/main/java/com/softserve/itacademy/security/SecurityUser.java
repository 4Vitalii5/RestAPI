package com.softserve.itacademy.security;

import lombok.Data;
import org.springframework.security.core.GrantedAuthority;
import com.softserve.itacademy.model.User;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Set;

@Data
public class SecurityUser implements UserDetails {

    private final long id;
    private final String username;
    private final String password;
    private final Set<? extends GrantedAuthority> authorities;

    public SecurityUser(long id, String username, String password, Set<? extends GrantedAuthority> authorities) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.authorities = authorities;
    }

    public long getId() { return id; }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    public static UserDetails fromUser(User user) {
        return new SecurityUser(
                user.getId(),
                user.getUsername(),
                user.getPassword(),
                user.getRole().getAuthorities()
        );
    }
}
