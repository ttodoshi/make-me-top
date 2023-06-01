package org.example.config;


import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.example.model.Person;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class JwtGeneratorImpl implements JwtGeneratorInterface {

    @Override
    public String generateToken(Person person) {
        Claims claims = Jwts.claims().setSubject(person.getPersonId().toString());
        claims.put("role", person.getRole());
        Date now = new Date();
        Date kill = new Date(now.getTime() + 55250 * 1000);

        return Jwts.builder().setClaims(claims)
                .setIssuedAt(now)
                .setExpiration(kill)
                .signWith(Keys.hmacShaKeyFor(Decoders.BASE64.decode("secretsecretsecretsecretsecretsecretsecretsecretsecretsecretsecretsecretsecretsecret")))
                .compact();
    }
}
