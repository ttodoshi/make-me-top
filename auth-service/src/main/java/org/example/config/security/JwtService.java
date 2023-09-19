package org.example.config.security;


import io.jsonwebtoken.Claims;
import org.example.dto.PersonDto;

import java.util.function.Function;

public interface JwtService {
    String generateToken(Integer personId, String role);

    String extractId(String jwtToken);

    <T> T extractClaim(String jwtToken, Function<Claims, T> claimsResolver);

    String extractRole(String jwtToken);

    boolean isTokenValid(String jwtToken, PersonDto person);
}
