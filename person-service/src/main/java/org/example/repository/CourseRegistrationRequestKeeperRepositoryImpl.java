package org.example.repository;

import lombok.RequiredArgsConstructor;
import org.example.dto.courserequest.CourseRegistrationRequestKeeperDto;
import org.example.exception.classes.connectEX.ConnectException;
import org.example.exception.classes.requestEX.RequestNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.List;

@Component
@RequiredArgsConstructor
public class CourseRegistrationRequestKeeperRepositoryImpl implements CourseRegistrationRequestKeeperRepository {
    private final WebClient.Builder webClientBuilder;
    private final AuthorizationHeaderRepository authorizationHeaderRepository;

    @Override
    public List<CourseRegistrationRequestKeeperDto> findCourseRegistrationRequestKeepersByRequestId(Integer requestId) {
        return webClientBuilder
                .baseUrl("http://course-registration-service/api/v1/course-registration-app/").build()
                .get()
                .uri(uri -> uri
                        .path("course-requests/{requestId}/keeper/")
                        .build(requestId)
                )
                .header("Authorization", authorizationHeaderRepository.getAuthorizationHeader())
                .retrieve()
                .onStatus(httpStatus -> httpStatus.isError() && !httpStatus.equals(HttpStatus.NOT_FOUND) && !httpStatus.equals(HttpStatus.UNAUTHORIZED), response -> {
                    throw new ConnectException();
                })
                .bodyToFlux(CourseRegistrationRequestKeeperDto.class)
                .timeout(Duration.ofSeconds(5))
                .onErrorResume(WebClientResponseException.Unauthorized.class, error -> Mono.error(new AccessDeniedException("Вам закрыт доступ к данной функциональности бортового компьютера")))
                .onErrorResume(WebClientResponseException.NotFound.class, error -> Mono.error(new RequestNotFoundException(requestId)))
                .collectList()
                .block();
    }

    @Override
    public List<CourseRegistrationRequestKeeperDto> findProcessingCourseRegistrationRequestKeepersByKeeperIdIn(List<Integer> keeperIds) {
        return webClientBuilder
                .baseUrl("http://course-registration-service/api/v1/course-registration-app/").build()
                .get()
                .uri(uri -> uri
                        .path("course-requests/keeper/")
                        .queryParam("keeperIds", keeperIds)
                        .build()
                )
                .header("Authorization", authorizationHeaderRepository.getAuthorizationHeader())
                .retrieve()
                .onStatus(httpStatus -> httpStatus.isError() && !httpStatus.equals(HttpStatus.UNAUTHORIZED), response -> {
                    throw new ConnectException();
                })
                .bodyToFlux(CourseRegistrationRequestKeeperDto.class)
                .timeout(Duration.ofSeconds(5))
                .onErrorResume(WebClientResponseException.Unauthorized.class, error -> Mono.error(new AccessDeniedException("Вам закрыт доступ к данной функциональности бортового компьютера")))
                .collectList()
                .block();
    }
}
