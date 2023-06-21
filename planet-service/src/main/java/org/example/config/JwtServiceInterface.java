package org.example.config;


import io.jsonwebtoken.Claims;
import org.example.model.Person;

import java.util.function.Function;

public interface JwtServiceInterface {

    String extractId(String jwtToken);

    <T> T extractClaim(String jwtToken, Function<Claims, T> claimsResolver);

    boolean isTokenValid(String jwtToken, Person person);
}
