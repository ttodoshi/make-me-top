package org.example.config.security;


import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import org.example.exception.classes.personEX.RoleNotAvailableException;
import org.example.model.AuthenticationRoleType;
import org.example.model.GeneralRole;
import org.example.model.GeneralRoleType;
import org.example.model.Person;
import org.example.repository.GeneralRoleRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.function.Function;

@Service
@RequiredArgsConstructor
@PropertySource(value = {"classpath:config.properties"})
public class JwtServiceImpl implements JwtServiceInterface {
    private final GeneralRoleRepository generalRoleRepository;
    @Value("${secret_key}")
    private String SECRET_KEY;

    @Override
    public String generateToken(Person person, String role) {
        Claims claims = Jwts.claims().setSubject(person.getPersonId().toString());
        if (isRoleAvailable(person, role))
            claims.put("role", role);
        else
            throw new RoleNotAvailableException();
        Date now = new Date();
        Date kill = new Date(now.getTime() + 43200 * 1000);
        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(now)
                .setExpiration(kill)
                .signWith(Keys.hmacShaKeyFor(
                        Decoders.BASE64.decode(SECRET_KEY)))
                .compact();
    }

    private boolean isRoleAvailable(Person person, String role) {
        if (role.equals(AuthenticationRoleType.EXPLORER.name()))
            return true;
        else if (role.equals(AuthenticationRoleType.KEEPER.name()) &&
                canBeKeeper(person.getPersonId()))
            return true;
        else return role.equals(GeneralRoleType.BIG_BROTHER.name()) &&
                    isBigBrother(person.getPersonId());
    }

    // TODO
    private boolean canBeKeeper(Integer personId) {
        return true;
    }

    private boolean isBigBrother(Integer personId) {
        for (GeneralRole role : generalRoleRepository.getRolesForPerson(personId)) {
            if (role.getName().equals(GeneralRoleType.BIG_BROTHER))
                return true;
        }
        return false;
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

    public String extractRole(String jwtToken) {
        return extractAllClaims(jwtToken).get("role", String.class);
    }

    private Claims extractAllClaims(String jwtToken) {
        return Jwts.parserBuilder()
                .setSigningKey(Keys.hmacShaKeyFor(
                        Decoders.BASE64.decode(SECRET_KEY)))
                .build()
                .parseClaimsJws(jwtToken)
                .getBody();
    }
}
