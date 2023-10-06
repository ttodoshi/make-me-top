package org.example.config.security;

import lombok.RequiredArgsConstructor;
import org.example.dto.PersonDto;
import org.example.exception.classes.personEX.PersonNotFoundException;
import org.example.repository.PersonRepository;
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
    private final PersonRepository personRepository;

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request,
                                    @NonNull HttpServletResponse response,
                                    @NonNull FilterChain filterChain) throws ServletException, IOException {
        Optional<String> jwtTokenOptional = getToken(request);
        final String jwtToken;
        if (jwtTokenOptional.isPresent())
            jwtToken = jwtTokenOptional.get().substring(7);
        else {
            filterChain.doFilter(request, response);
            return;
        }

        final String userId = jwtService.extractId(jwtToken);
        if (userId != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            Optional<PersonDto> personOptional = personRepository.findById(Integer.valueOf(userId));
            if (personOptional.isEmpty())
                throw new PersonNotFoundException();
            PersonDto person = personOptional.get();
            if (jwtService.isTokenValid(jwtToken, person)) {
                UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                        person,
                        null,
                        List.of(new SimpleGrantedAuthority(jwtService.extractRole(jwtToken)))
                );
                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authToken);
            }
        }
        filterChain.doFilter(request, response);
    }

    private Optional<String> getToken(HttpServletRequest request) {
        return Optional.ofNullable(request.getHeader("Authorization"));
    }
}
