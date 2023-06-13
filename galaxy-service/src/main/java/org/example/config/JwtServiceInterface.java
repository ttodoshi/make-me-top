package org.example.config;


import io.jsonwebtoken.Claims;
import org.example.model.person.Person;

import java.util.function.Function;

public interface JwtServiceInterface {

    String generateToken(Person person);

    String extractId(String jwtToken);

    <T> T extractClaim(String jwtToken, Function<Claims, T> claimsResolver);

    boolean isTokenValid(String jwtToken, Person person);
}
