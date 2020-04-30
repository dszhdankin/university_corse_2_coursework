package com.code_generator_server.entity;

import org.springframework.security.core.GrantedAuthority;

public class Role implements GrantedAuthority {

    @Override
    public String getAuthority() {
        return "ROLE_USER";
    }
}
