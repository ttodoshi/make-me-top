package org.example.config.security;


import io.jsonwebtoken.Claims;
import org.example.dto.PersonDto;
import org.example.dto.token.AccessTokenDto;
import org.example.dto.token.RefreshTokenDto;

import java.util.function.Function;

public interface JwtService {
    AccessTokenDto generateAccessToken(Integer personId, String role);

    RefreshTokenDto generateRefreshToken();

    String extractId(String jwtToken);

    <T> T extractClaim(String jwtToken, Function<Claims, T> claimsResolver);

    String extractRole(String jwtToken);

    boolean isAccessTokenValid(String jwtToken, PersonDto person);

    boolean isRefreshTokenValid(String jwtToken);
}
