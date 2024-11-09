package com.timebuddy.models;

import org.springframework.security.core.GrantedAuthority;


public enum Role implements GrantedAuthority {
    ROLE_USER,
    ROLE_ADMIN;

    @Override
    public String getAuthority() {
        return name(); // Returnerar enumets namn ("ROLE_USER", "ROLE_ADMIN")
    }
}
