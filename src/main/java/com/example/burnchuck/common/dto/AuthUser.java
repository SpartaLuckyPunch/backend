package com.example.burnchuck.common.dto;

import java.util.Collection;
import java.util.List;

import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

@Getter
public class AuthUser {

    private final Long id;
    private final String email;
    private final String nickname;
    private final String role;
    private final Collection<? extends GrantedAuthority> authorities;


    public AuthUser(Long id, String email, String nickname, String role) {
        this.id = id;
        this.email = email;
        this.nickname = nickname;
        this.role = role;
        this.authorities = List.of(new SimpleGrantedAuthority("ROLE_USER"));
    }
}
