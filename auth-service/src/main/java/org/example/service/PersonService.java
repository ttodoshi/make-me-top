package org.example.service;

import org.example.config.security.JwtService;
import org.example.config.security.role.RoleChecker;
import org.example.dto.AuthResponse;
import org.example.dto.AuthResponseEmployee;
import org.example.dto.LoginRequest;
import org.example.exception.classes.connectEX.ConnectException;
import org.example.exception.classes.personEX.PersonNotFoundException;
import org.example.exception.classes.personEX.RoleNotAvailableException;
import org.example.model.Person;
import org.example.repository.AuthorizationHeaderRepository;
import org.example.repository.PersonRepository;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

@Service
public class PersonService {
    private final PersonRepository personRepository;
    private final AuthorizationHeaderRepository mmtrAuthorizationHeaderRepository;

    private final JwtService jwtGenerator;
    private final WebClient.Builder webClientBuilder;
    private final Map<String, RoleChecker> roleCheckerMap;
    @Value("${mmtr-auth-url}")
    private String MMTR_AUTH_URL;

    public PersonService(PersonRepository personRepository, AuthorizationHeaderRepository mmtrAuthorizationHeaderRepository, JwtService jwtGenerator, WebClient.Builder webClientBuilder,
                         @Qualifier("roleCheckerMap") Map<String, RoleChecker> roleCheckerMap) {
        this.personRepository = personRepository;
        this.mmtrAuthorizationHeaderRepository = mmtrAuthorizationHeaderRepository;
        this.jwtGenerator = jwtGenerator;
        this.webClientBuilder = webClientBuilder;
        this.roleCheckerMap = roleCheckerMap;
    }

    public String login(LoginRequest request, HttpServletResponse response) {
        Person person = authenticatePerson(request);
        if (!roleCheckerMap.get(request.getRole()).isRoleAvailable(person.getPersonId()))
            throw new RoleNotAvailableException();
        String token = jwtGenerator.generateToken(person, request.getRole());
        Cookie tokenCookie = generateCookie(token);
        response.addCookie(tokenCookie);
        return token;
    }

    private Person authenticatePerson(LoginRequest loginRequest) {
        AuthResponse response = sendAuthenticateRequest(loginRequest);
        mmtrAuthorizationHeaderRepository.setAuthorizationHeader(
                "Bearer " + response.getObject().getUserToken().getTokenInfo()
        );
        if (!response.getIsSuccess())
            throw new PersonNotFoundException();
        return findPerson(response.getObject());
    }

    private AuthResponse sendAuthenticateRequest(LoginRequest loginRequest) {
        return webClientBuilder.baseUrl(MMTR_AUTH_URL).build()
                .post()
                .uri("ts-rest/SingleSignOn/authorization/")
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

    private Person findPerson(AuthResponseEmployee employee) {
        return personRepository.findById(employee.getEmployeeId()).orElseGet(
                () -> personRepository.save(
                        Person.builder()
                                .personId(employee.getEmployeeId())
                                .firstName(employee.getFirstName())
                                .lastName(employee.getLastName())
                                .patronymic(employee.getPatronymic())
                                .maxExplorers(0)
                                .build()
                )
        );
    }

    private Cookie generateCookie(String token) {
        Cookie tokenCookie = new Cookie("token", token);
        tokenCookie.setMaxAge(43200);
        tokenCookie.setPath("/");
        return tokenCookie;
    }

    public Map<String, Object> logout(HttpServletResponse response) {
        Cookie tokenCookie = new Cookie("token", "");
        tokenCookie.setMaxAge(0);
        tokenCookie.setPath("/");
        response.addCookie(tokenCookie);
        Map<String, Object> jsonResponse = new HashMap<>();
        jsonResponse.put("message", "Выход успешный");
        return jsonResponse;
    }
}
