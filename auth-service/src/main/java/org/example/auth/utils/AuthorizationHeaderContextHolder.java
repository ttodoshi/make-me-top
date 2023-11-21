package org.example.auth.utils;

public interface AuthorizationHeaderContextHolder {
    String getAuthorizationHeader();

    void setAuthorizationHeader(String token);
}
