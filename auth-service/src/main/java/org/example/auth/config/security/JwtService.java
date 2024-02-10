package org.example.auth.config.security;

import org.example.auth.dto.token.AccessTokenDto;
import org.example.auth.dto.token.RefreshTokenDto;

public interface JwtService {
    AccessTokenDto generateAccessToken(Long personId, String role);

    RefreshTokenDto generateRefreshToken(Long personId);

    String extractAccessTokenId(String accessToken);

    String extractAccessTokenRole(String accessToken);

    boolean isAccessTokenValid(String accessToken);

    boolean isRefreshTokenValid(String refreshToken);
}
