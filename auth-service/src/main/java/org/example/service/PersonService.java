package org.example.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.config.mapper.PersonMapper;
import org.example.config.security.JwtServiceInterface;
import org.example.dto.AuthResponseUser;
import org.example.dto.LoginRequest;
import org.example.dto.MmtrAuthResponse;
import org.example.exception.classes.connectEX.ConnectException;
import org.example.exception.classes.personEX.PersonNotFoundException;
import org.example.model.Person;
import org.example.repository.PersonRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import java.nio.charset.StandardCharsets;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class PersonService {
    private final PersonRepository personRepository;

    private final JwtServiceInterface jwtGenerator;

    private final PersonMapper personMapper;

    @Value("${url_auth_mmtr}")
    private String MMTR_AUTH_URL;

    public Object login(LoginRequest request, HttpServletResponse response) {
        Person person = authenticatePerson(request);
        String token = jwtGenerator.generateToken(person, request.getRole());
        Cookie tokenCookie = generateCookie(token);
        response.addCookie(tokenCookie);
        return token;
    }

    private Person authenticatePerson(LoginRequest request) {
        Optional<AuthResponseUser> authResponseOptional = sendAuthRequest(request);
        if (authResponseOptional.isEmpty())
            throw new PersonNotFoundException();
        AuthResponseUser authResponse = authResponseOptional.get();
        return personRepository.findById(authResponse.getEmployeeId()).orElseGet(
                () -> personRepository.save(personMapper.UserAuthResponseToPerson(authResponse))
        );
    }

    private Cookie generateCookie(String token) {
        Cookie tokenCookie = new Cookie("token", token);
        tokenCookie.setMaxAge(43200);
        tokenCookie.setPath("/");
        return tokenCookie;
    }

    public Optional<AuthResponseUser> sendAuthRequest(LoginRequest userRequest) {
        WebClient webClient = WebClient.create(MMTR_AUTH_URL);
        MmtrAuthResponse response = webClient.post()
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(userRequest)
                .acceptCharset(StandardCharsets.UTF_8)
                .retrieve()
                .onStatus(HttpStatus.NOT_FOUND::equals, e -> Mono.error(new PersonNotFoundException()))
                .bodyToMono(MmtrAuthResponse.class)
                .doOnError(ConnectException::new)
                .block();
        Optional<AuthResponseUser> employeeOptional = Optional.empty();
        if (response != null && response.getIsSuccess())
            employeeOptional = Optional.of(response.getObject());
        return employeeOptional;
    }
}
