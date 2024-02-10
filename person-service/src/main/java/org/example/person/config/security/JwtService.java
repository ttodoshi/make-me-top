package org.example.person.config.security;


public interface JwtService {
    String extractAccessTokenId(String accessToken);

    String extractAccessTokenRole(String accessToken);

    boolean isAccessTokenValid(String accessToken);
}
