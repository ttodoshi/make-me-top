package org.example.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.config.mapper.PersonMapper;
import org.example.config.security.JwtServiceInterface;
import org.example.dto.AuthResponse;
import org.example.dto.AuthResponseEmployee;
import org.example.dto.LoginRequest;
import org.example.exception.classes.connectEX.ConnectException;
import org.example.exception.classes.personEX.PersonNotFoundException;
import org.example.model.Person;
import org.example.repository.PersonRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;

@Service
@RequiredArgsConstructor
@Slf4j
public class PersonService {
    private final PersonRepository personRepository;

    private final JwtServiceInterface jwtGenerator;

    private final PersonMapper personMapper;

    @Value("${url_auth_mmtr}")
    private String MMTR_AUTH_URL;

    public String login(LoginRequest request, HttpServletResponse response) {
        Person person = authenticatePerson(request);
        String token = jwtGenerator.generateToken(person, request.getRole());
        Cookie tokenCookie = generateCookie(token);
        response.addCookie(tokenCookie);
        return token;
    }

    private Person authenticatePerson(LoginRequest loginRequest) {
        AuthResponseEmployee authResponse = authenticateEmployee(loginRequest);
        return personRepository.findById(authResponse.getEmployeeId()).orElseGet(
                () -> personRepository.save(personMapper.UserAuthResponseToPerson(authResponse))
        );
    }

    private AuthResponseEmployee authenticateEmployee(LoginRequest loginRequest) {
        AuthResponse authResponse = sendAuthenticateRequest(loginRequest);
        if (authResponse.getIsSuccess())
            return authResponse.getObject();
        throw new PersonNotFoundException();
    }

    private AuthResponse sendAuthenticateRequest(LoginRequest loginRequest) {
        WebClient webClient = WebClient.create(MMTR_AUTH_URL);
        return webClient.post()
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(loginRequest)
                .acceptCharset(StandardCharsets.UTF_8)
                .retrieve()
                .bodyToMono(AuthResponse.class)
                .timeout(Duration.ofSeconds(5))
                .onErrorResume(throwable -> {
                    throw new ConnectException();
                })
                .block();
    }

    private Cookie generateCookie(String token) {
        Cookie tokenCookie = new Cookie("token", token);
        tokenCookie.setMaxAge(43200);
        tokenCookie.setPath("/");
        return tokenCookie;
    }
}
