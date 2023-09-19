package org.example.service.implementations;

import lombok.RequiredArgsConstructor;
import org.example.dto.person.PersonDto;
import org.example.dto.person.UpdatePersonDto;
import org.example.exception.classes.connectEX.ConnectException;
import org.example.repository.AuthorizationHeaderRepository;
import org.example.repository.PersonRepository;
import org.example.service.PersonService;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.Duration;

@Component
@RequiredArgsConstructor
public class PersonServiceImpl implements PersonService {
    private final PersonRepository personRepository;
    private final AuthorizationHeaderRepository authorizationHeaderRepository;
    private final WebClient.Builder webClientBuilder;

    @Override
    public void setDefaultExplorersValueForPerson(Integer personId) {
        personRepository.findById(personId).ifPresent(
                p -> webClientBuilder
                        .baseUrl("http://person-service/api/v1/person-app/").build()
                        .post()
                        .uri(uri -> uri
                                .path("person/{personId}/")
                                .build(personId)
                        )
                        .header("Authorization", authorizationHeaderRepository.getAuthorizationHeader())
                        .bodyValue(new UpdatePersonDto(3))
                        .retrieve()
                        .onStatus(HttpStatus::isError, response -> {
                            throw new ConnectException();
                        })
                        .bodyToMono(PersonDto.class)
                        .timeout(Duration.ofSeconds(5))
                        .block());
    }
}
