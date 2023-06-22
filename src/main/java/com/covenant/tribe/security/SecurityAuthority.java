package com.covenant.tribe.security;

import com.covenant.tribe.domain.user.Authority;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.security.core.GrantedAuthority;

@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class SecurityAuthority implements GrantedAuthority {

    Authority authority;

    @Override
    public String getAuthority() {
        return authority.getName();
    }
}
