package org.example.person.config.security;

import lombok.RequiredArgsConstructor;
import net.devh.boot.grpc.server.security.authentication.BearerAuthenticationReader;
import net.devh.boot.grpc.server.security.authentication.GrpcAuthenticationReader;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.List;

@Configuration
@RequiredArgsConstructor
public class GrpcSecurityConfig {
    private final JwtService jwtService;

    @Bean
    public GrpcAuthenticationReader grpcAuthenticationReader() {
        return new BearerAuthenticationReader(accessToken -> {
            if (jwtService.isAccessTokenValid(accessToken)) {
                final String personId = jwtService.extractAccessTokenId(accessToken);
                return new UsernamePasswordAuthenticationToken(
                        Long.valueOf(personId),
                        accessToken,
                        List.of(
                                new SimpleGrantedAuthority(
                                        jwtService.extractAccessTokenRole(accessToken)
                                )
                        )
                );
            }
            return null;
        });
    }
}
