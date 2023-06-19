package org.example.config;


import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.example.model.Person;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.function.Function;

@Service
@PropertySource(value = {"classpath:config.properties"})
public class JwtServiceImpl implements JwtServiceInterface {
    @Value("${secret_key}")
    private String SECRET_KEY;

    @Override
    public String generateToken(Person person) {
        Claims claims = Jwts.claims().setSubject(person.getPersonId().toString());
        claims.put("role", person.getRole());
        Date now = new Date();
        Date kill = new Date(now.getTime() + 55250 * 1000);
        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(now)
                .setExpiration(kill)
                .signWith(Keys.hmacShaKeyFor(
                        Decoders.BASE64.decode(SECRET_KEY)))
                .compact();
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

    public boolean isTokenValid(String jwtToken, Person person) {
        final String id = extractId(jwtToken);
        return id.equals(person.getPersonId().toString()) && !isTokenExpired(jwtToken);
    }

    private boolean isTokenExpired(String jwtToken) {
        return extractExpiration(jwtToken).before(new Date());
    }

    private Date extractExpiration(String jwtToken) {
        return extractClaim(jwtToken, Claims::getExpiration);
    }

    private Claims extractAllClaims(String jwtToken) {
        return Jwts.parserBuilder()
                .setSigningKey(SECRET_KEY)
                .build()
                .parseClaimsJws(jwtToken)
                .getBody();
    }
}
