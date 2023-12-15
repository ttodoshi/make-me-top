package org.example.auth.config.security;

import lombok.RequiredArgsConstructor;
import org.example.auth.service.PersonService;
import org.example.grpc.PeopleService;
import org.springframework.http.HttpHeaders;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class JwtAuthFilter extends OncePerRequestFilter {
    private final JwtService jwtService;
    private final PersonService personService;

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request,
                                    @NonNull HttpServletResponse response,
                                    @NonNull FilterChain filterChain) throws ServletException, IOException {
        final Optional<String> accessTokenOptional = getToken(request);
        final String accessToken;
        if (accessTokenOptional.isPresent())
            accessToken = accessTokenOptional.get().substring(7);
        else {
            filterChain.doFilter(request, response);
            return;
        }

        final String userId = jwtService.extractId(accessToken);
        if (userId != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            PeopleService.Person person = personService.findPersonById(Long.valueOf(userId));
            if (jwtService.isTokenValid(accessToken)) {
                UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                        person,
                        accessToken,
                        List.of(new SimpleGrantedAuthority(jwtService.extractRole(accessToken)))
                );
                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authToken);
            }
        }
        filterChain.doFilter(request, response);
    }

    private Optional<String> getToken(HttpServletRequest request) {
        return Optional.ofNullable(request.getHeader(HttpHeaders.AUTHORIZATION));
    }
}
