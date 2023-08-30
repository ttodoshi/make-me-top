package org.example.config.security;


import io.jsonwebtoken.Claims;
import org.example.model.Person;

import java.util.function.Function;

public interface JwtService {

    String extractId(String jwtToken);

    <T> T extractClaim(String jwtToken, Function<Claims, T> claimsResolver);

    String extractRole(String jwtToken);

    boolean isTokenValid(String jwtToken, Person person);
}
