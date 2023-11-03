package org.example.repository;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
public class AuthorizationHeaderRepositoryImpl implements AuthorizationHeaderRepository {
    @Override
    public String getAuthorizationHeader() {
        String accessToken = (String) SecurityContextHolder.getContext().getAuthentication().getCredentials();
        return "Bearer " + accessToken;
    }
}
