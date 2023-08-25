package org.example.service;

import lombok.RequiredArgsConstructor;
import org.example.dto.AuthResponse;
import org.example.dto.AuthResponseEmployee;
import org.example.dto.LoginRequest;
import org.example.exception.classes.connectEX.ConnectException;
import org.example.exception.classes.personEX.PersonNotFoundException;
import org.example.model.Person;
import org.example.repository.AuthorizationHeaderRepository;
import org.example.repository.PersonRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.nio.charset.StandardCharsets;
import java.time.Duration;

@Service
@RequiredArgsConstructor
public class PersonService {
    private final PersonRepository personRepository;
    private final AuthorizationHeaderRepository mmtrAuthorizationHeaderRepository;

    private final WebClient.Builder webClientBuilder;
    @Value("${mmtr-auth-url}")
    private String MMTR_AUTH_URL;

    public Person authenticatePerson(LoginRequest loginRequest) {
        AuthResponse response = sendAuthenticateRequest(loginRequest);
        mmtrAuthorizationHeaderRepository.setAuthorizationHeader(
                "Bearer " + response.getObject().getUserToken().getTokenInfo());
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
        return personRepository.findById(employee.getEmployeeId())
                .orElseGet(
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
}
