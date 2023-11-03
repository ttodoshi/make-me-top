package org.example.config.security;


import io.jsonwebtoken.Claims;
import org.example.grpc.PeopleService;

import java.util.function.Function;

public interface JwtService {

    String extractId(String jwtToken);

    <T> T extractClaim(String jwtToken, Function<Claims, T> claimsResolver);

    String extractRole(String jwtToken);
    // TODO fix everywhere, only token

    boolean isTokenValid(String jwtToken, PeopleService.Person person);
}
