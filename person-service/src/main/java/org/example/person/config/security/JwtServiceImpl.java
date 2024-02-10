package org.example.person.config.security;


import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.function.Function;

@Service
@RequiredArgsConstructor
public class JwtServiceImpl implements JwtService {
    @Value("${access-token-secret-key}")
    private String ACCESS_TOKEN_SECRET_KEY;

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
}
