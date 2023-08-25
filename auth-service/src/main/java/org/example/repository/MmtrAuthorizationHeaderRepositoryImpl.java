package org.example.repository;

import org.springframework.stereotype.Component;

@Component
public class MmtrAuthorizationHeaderRepositoryImpl implements AuthorizationHeaderRepository {
    private String authorizationHeader;

    @Override
    public String getAuthorizationHeader() {
        return authorizationHeader;
    }

    @Override
    public void setAuthorizationHeader(String authorizationHeader) {
        this.authorizationHeader = authorizationHeader;
    }
}
