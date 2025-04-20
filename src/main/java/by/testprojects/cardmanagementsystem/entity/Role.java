package by.testprojects.cardmanagementsystem.security.entity;

import org.springframework.security.core.GrantedAuthority;

import static by.testprojects.cardmanagementsystem.Constants.AUTHORITY_PREFIX;

public enum Role implements GrantedAuthority {
    USER,
    ADMIN;

    @Override
    public String getAuthority() {
        return AUTHORITY_PREFIX + name();
    }
}
