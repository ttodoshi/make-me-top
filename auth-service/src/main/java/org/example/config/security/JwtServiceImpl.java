package org.example.config.security;


import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.example.dto.token.AccessTokenDto;
import org.example.dto.token.RefreshTokenDto;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.function.Function;

@Service
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
    public AccessTokenDto generateAccessToken(Integer personId, String role) {
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
    public RefreshTokenDto generateRefreshToken(Integer personId) {
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
    public String extractId(String jwtToken) {
        return extractClaim(jwtToken, Claims::getSubject);
    }

    @Override
    public <T> T extractClaim(String jwtToken, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(jwtToken);
        return claimsResolver.apply(claims);
    }

    @Override
    public boolean isAccessTokenValid(String jwtToken) {
        return !isTokenExpired(jwtToken);
    }

    @Override
    public boolean isRefreshTokenValid(String jwtToken) {
        return !isTokenExpired(jwtToken);
    }

    private boolean isTokenExpired(String jwtToken) {
        return extractExpiration(jwtToken).before(new Date());
    }

    private Date extractExpiration(String jwtToken) {
        return extractClaim(jwtToken, Claims::getExpiration);
    }

    @Override
    public String extractRole(String jwtToken) {
        return extractAllClaims(jwtToken).get("role", String.class);
    }

    private Claims extractAllClaims(String jwtToken) {
        return Jwts.parserBuilder()
                .setSigningKey(Keys.hmacShaKeyFor(
                        Decoders.BASE64.decode(ACCESS_TOKEN_SECRET_KEY)))
                .build()
                .parseClaimsJws(jwtToken)
                .getBody();
    }
}
