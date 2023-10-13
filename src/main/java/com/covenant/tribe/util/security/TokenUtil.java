package com.covenant.tribe.util.security;

import org.springframework.security.core.context.SecurityContext;

public class TokenUtil {

    public static Long getUserIdFromToken(SecurityContext context) {
        return Long.valueOf(context.getAuthentication().getName());
    }

}

