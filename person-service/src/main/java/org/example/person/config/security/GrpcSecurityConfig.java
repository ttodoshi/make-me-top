package org.example.person.config.security;

import lombok.RequiredArgsConstructor;
import net.devh.boot.grpc.server.security.authentication.BearerAuthenticationReader;
import net.devh.boot.grpc.server.security.authentication.GrpcAuthenticationReader;
import org.example.person.model.Person;
import org.example.person.service.PersonService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.List;

@Configuration
@RequiredArgsConstructor
public class GrpcSecurityConfig {
    private final PersonService personService;
    private final JwtService jwtService;

    @Bean
    public GrpcAuthenticationReader grpcAuthenticationReader() {
        return new BearerAuthenticationReader(accessToken -> {
            if (jwtService.isTokenValid(accessToken)) {
                final String userId = jwtService.extractId(accessToken);
                Person person = personService.findPersonById(Long.valueOf(userId));
                return new UsernamePasswordAuthenticationToken(
                        person,
                        accessToken,
                        List.of(new SimpleGrantedAuthority(jwtService.extractRole(accessToken)))
                );
            }
            return null;
        });
    }
}
