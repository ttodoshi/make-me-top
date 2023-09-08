package org.example.service.implementations;

import lombok.RequiredArgsConstructor;
import org.example.dto.AuthResponseDto;
import org.example.dto.AuthResponseEmployeeDto;
import org.example.dto.LoginRequestDto;
import org.example.exception.classes.connectEX.ConnectException;
import org.example.exception.classes.personEX.PersonNotFoundException;
import org.example.model.Person;
import org.example.repository.AuthorizationHeaderRepository;
import org.example.repository.PersonRepository;
import org.example.service.PersonService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.nio.charset.StandardCharsets;
import java.time.Duration;

@Service
@RequiredArgsConstructor
public class PersonServiceImpl implements PersonService {
    private final PersonRepository personRepository;
    private final AuthorizationHeaderRepository mmtrAuthorizationHeaderRepository;

    private final WebClient.Builder webClientBuilder;
    @Value("${mmtr-auth-url}")
    private String MMTR_AUTH_URL;

    @Override
    public Person authenticatePerson(LoginRequestDto loginRequestDto) {
        AuthResponseDto response = sendAuthenticateRequest(loginRequestDto);
        if (!response.getIsSuccess())
            throw new PersonNotFoundException();
        mmtrAuthorizationHeaderRepository.setAuthorizationHeader(
                "Bearer " + response.getObject().getUserToken().getTokenInfo());
        return findPerson(response.getObject());
    }

    private AuthResponseDto sendAuthenticateRequest(LoginRequestDto loginRequestDto) {
        return webClientBuilder.baseUrl(MMTR_AUTH_URL).build()
                .post()
                .uri("ts-rest/SingleSignOn/authorization/")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(loginRequestDto)
                .acceptCharset(StandardCharsets.UTF_8)
                .retrieve()
                .bodyToMono(AuthResponseDto.class)
                .timeout(Duration.ofSeconds(5))
                .onErrorResume(throwable -> {
                    throw new ConnectException();
                })
                .block();
    }

    private Person findPerson(AuthResponseEmployeeDto employee) {
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
