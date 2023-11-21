package org.example.person.utils;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
public class AuthorizationHeaderContextHolderImpl implements AuthorizationHeaderContextHolder {
    @Override
    public String getAuthorizationHeader() {
        return "Bearer " + SecurityContextHolder.getContext().getAuthentication().getCredentials();
    }
}
