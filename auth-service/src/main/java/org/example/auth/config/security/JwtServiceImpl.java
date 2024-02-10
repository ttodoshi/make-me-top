package org.example.auth.config.security;


import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import org.example.auth.dto.token.AccessTokenDto;
import org.example.auth.dto.token.RefreshTokenDto;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.function.Function;

@Service
@RequiredArgsConstructor
public class JwtServiceImpl implements JwtService {
    @Value("${access-token-life-time-seconds}")
    private Integer ACCESS_TOKEN_LIFE_TIME;
    @Value("${refresh-token-life-time-seconds}")
    private Integer REFRESH_TOKEN_LIFE_TIME;
    @Value("${access-token-secret-key}")
    private String ACCESS_TOKEN_SECRET_KEY;
    @Value("${refresh-token-secret-key}")
    private String REFRESH_TOKEN_SECRET_KEY;

    @Override
    public AccessTokenDto generateAccessToken(Long personId, String role) {
        Claims claims = Jwts.claims().setSubject(String.valueOf(personId));
        claims.put("role", role);
        Date now = new Date();
        Date kill = new Date(now.getTime() + ACCESS_TOKEN_LIFE_TIME * 1000);
        return new AccessTokenDto(
                Jwts.builder()
                        .setClaims(claims)
                        .setIssuedAt(now)
                        .setExpiration(kill)
                        .signWith(Keys.hmacShaKeyFor(
                                Decoders.BASE64.decode(ACCESS_TOKEN_SECRET_KEY)))
                        .compact(),
                kill
        );
    }

    @Override
    public String extractAccessTokenId(String accessToken) {
        return extractAccessTokenClaim(accessToken, Claims::getSubject);
    }

    @Override
    public String extractAccessTokenRole(String accessToken) {
        return extractAccessTokenClaim(
                accessToken,
                claims -> claims.get("role", String.class)
        );
    }

    @Override
    public boolean isAccessTokenValid(String accessToken) {
        return extractAccessTokenClaim(accessToken, Claims::getExpiration).after(new Date());
    }

    private <T> T extractAccessTokenClaim(String accessToken, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllAccessTokenClaims(accessToken);
        return claimsResolver.apply(claims);
    }

    private Claims extractAllAccessTokenClaims(String accessToken) {
        return Jwts.parserBuilder()
                .setSigningKey(Keys.hmacShaKeyFor(
                        Decoders.BASE64.decode(ACCESS_TOKEN_SECRET_KEY)))
                .build()
                .parseClaimsJws(accessToken)
                .getBody();
    }

    @Override
    public RefreshTokenDto generateRefreshToken(Long personId) {
        Claims claims = Jwts.claims().setSubject(String.valueOf(personId));
        Date now = new Date();
        Date kill = new Date(now.getTime() + REFRESH_TOKEN_LIFE_TIME * 1000);
        return new RefreshTokenDto(
                Jwts.builder()
                        .setClaims(claims)
                        .setHeaderParam("millis", now.getTime())
                        .setIssuedAt(now)
                        .setExpiration(kill)
                        .signWith(Keys.hmacShaKeyFor(
                                Decoders.BASE64.decode(REFRESH_TOKEN_SECRET_KEY)))
                        .compact(),
                kill
        );
    }

    @Override
    public boolean isRefreshTokenValid(String refreshToken) {
        return extractRefreshTokenClaim(refreshToken, Claims::getExpiration).after(new Date());
    }

    private <T> T extractRefreshTokenClaim(String refreshToken, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllRefreshTokenClaims(refreshToken);
        return claimsResolver.apply(claims);
    }

    private Claims extractAllRefreshTokenClaims(String refreshToken) {
        return Jwts.parserBuilder()
                .setSigningKey(Keys.hmacShaKeyFor(
                        Decoders.BASE64.decode(REFRESH_TOKEN_SECRET_KEY)))
                .build()
                .parseClaimsJws(refreshToken)
                .getBody();
    }
}
