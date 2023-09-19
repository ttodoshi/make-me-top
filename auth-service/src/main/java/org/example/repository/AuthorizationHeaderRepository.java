package org.example.repository;

public interface AuthorizationHeaderRepository {
    String getAuthorizationHeader();

    void setAuthorizationHeader(String token);
}
