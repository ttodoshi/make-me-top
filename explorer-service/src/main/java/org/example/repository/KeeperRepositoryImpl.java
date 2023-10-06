package org.example.repository;

import lombok.RequiredArgsConstructor;
import org.example.dto.keeper.KeeperDto;
import org.example.exception.classes.connectEX.ConnectException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class KeeperRepositoryImpl implements KeeperRepository {
    private final WebClient.Builder webClientBuilder;
    private final AuthorizationHeaderRepository authorizationHeaderRepository;

    public Optional<KeeperDto> findKeeperByPersonIdAndCourseId(Integer personId, Integer courseId) {
        return webClientBuilder
                .baseUrl("http://keeper-service/api/v1/keeper-app/").build()
                .get()
                .uri(uri -> uri
                        .path("keeper/")
                        .queryParam("personId", personId)
                        .queryParam("courseId", courseId)
                        .build()
                )
                .header("Authorization", authorizationHeaderRepository.getAuthorizationHeader())
                .retrieve()
                .onStatus(httpStatus -> httpStatus.isError() && !httpStatus.equals(HttpStatus.NOT_FOUND), response -> {
                    throw new ConnectException();
                })
                .bodyToMono(KeeperDto.class)
                .timeout(Duration.ofSeconds(5))
                .onErrorResume(WebClientResponseException.NotFound.class, error -> Mono.empty())
                .blockOptional();
    }

    @Override
    public Boolean existsById(Integer keeperId) {
        return webClientBuilder
                .baseUrl("http://keeper-service/api/v1/keeper-app/").build()
                .get()
                .uri(uri -> uri
                        .path("keeper/{keeperId}/")
                        .build(keeperId)
                )
                .header("Authorization", authorizationHeaderRepository.getAuthorizationHeader())
                .exchangeToMono(
                        r -> {
                            if (r.statusCode().is2xxSuccessful())
                                return Mono.just(true);
                            else if (r.statusCode().equals(HttpStatus.NOT_FOUND))
                                return Mono.just(false);
                            else return Mono.error(new ConnectException());
                        }
                )
                .timeout(Duration.ofSeconds(5))
                .block();
    }
}
