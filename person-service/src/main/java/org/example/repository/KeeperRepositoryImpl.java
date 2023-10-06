package org.example.repository;

import lombok.RequiredArgsConstructor;
import org.example.dto.keeper.KeeperDto;
import org.example.exception.classes.connectEX.ConnectException;
import org.example.exception.classes.personEX.PersonNotFoundException;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.List;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class KeeperRepositoryImpl implements KeeperRepository {
    private final WebClient.Builder webClientBuilder;
    private final AuthorizationHeaderRepository authorizationHeaderRepository;

    @Override
    public List<KeeperDto> findKeepersByPersonId(Integer personId) {
        return webClientBuilder
                .baseUrl("http://keeper-service/api/v1/keeper-app/").build()
                .get()
                .uri(uri -> uri
                        .path("keeper/")
                        .queryParam("personId", personId)
                        .build()
                )
                .header("Authorization", authorizationHeaderRepository.getAuthorizationHeader())
                .retrieve()
                .onStatus(httpStatus -> httpStatus.isError() && !httpStatus.equals(HttpStatus.NOT_FOUND), response -> {
                    throw new ConnectException();
                })
                .bodyToFlux(KeeperDto.class)
                .timeout(Duration.ofSeconds(5))
                .onErrorResume(WebClientResponseException.NotFound.class, error -> Mono.error(new PersonNotFoundException(personId)))
                .collectList()
                .block();
    }

    @Override
    public Map<Integer, KeeperDto> findKeepersByKeeperIdIn(List<Integer> keeperIds) {
        return webClientBuilder
                .baseUrl("http://keeper-service/api/v1/keeper-app/").build()
                .get()
                .uri(uri -> uri
                        .path("keepers/")
                        .queryParam("keeperIds", keeperIds)
                        .build()
                )
                .header("Authorization", authorizationHeaderRepository.getAuthorizationHeader())
                .retrieve()
                .onStatus(HttpStatus::isError, response -> {
                    throw new ConnectException();
                })
                .bodyToFlux(new ParameterizedTypeReference<Map<Integer, KeeperDto>>() {
                })
                .timeout(Duration.ofSeconds(5))
                .blockLast();
    }

    @Override
    public KeeperDto getReferenceById(Integer keeperId) {
        return webClientBuilder
                .baseUrl("http://keeper-service/api/v1/keeper-app/").build()
                .get()
                .uri(uri -> uri
                        .path("keeper/{keeperId}/")
                        .build(keeperId)
                )
                .header("Authorization", authorizationHeaderRepository.getAuthorizationHeader())
                .retrieve()
                .onStatus(httpStatus -> httpStatus.isError() && !httpStatus.equals(HttpStatus.NOT_FOUND), response -> {
                    throw new ConnectException();
                })
                .bodyToMono(KeeperDto.class)
                .timeout(Duration.ofSeconds(5))
                .block();
    }
}
