package org.example.progress.config.security;


public interface JwtService {
    String extractAccessTokenId(String accessToken);

    String extractAccessTokenRole(String accessToken);

    boolean isAccessTokenValid(String accessToken);
}
