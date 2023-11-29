package org.example.person.repository.implementations;

import lombok.RequiredArgsConstructor;
import org.example.person.dto.courserequest.CourseRegistrationRequestKeeperDto;
import org.example.person.exception.classes.connect.ConnectException;
import org.example.person.exception.classes.request.RequestNotFoundException;
import org.example.person.repository.CourseRegistrationRequestKeeperRepository;
import org.example.person.utils.AuthorizationHeaderContextHolder;
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
    private final AuthorizationHeaderContextHolder authorizationHeaderContextHolder;

    @Override
    public List<CourseRegistrationRequestKeeperDto> findCourseRegistrationRequestKeepersByRequestId(Long requestId) {
        return webClientBuilder
                .baseUrl("http://course-registration-service/api/v1/course-registration-app/").build()
                .get()
                .uri(uri -> uri
                        .path("course-requests/{requestId}/keepers/")
                        .build(requestId)
                )
                .header("Authorization", authorizationHeaderContextHolder.getAuthorizationHeader())
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
    public List<CourseRegistrationRequestKeeperDto> findProcessingCourseRegistrationRequestKeepersByKeeperIdIn(List<Long> keeperIds) {
        return webClientBuilder
                .baseUrl("http://course-registration-service/api/v1/course-registration-app/").build()
                .get()
                .uri(uri -> uri
                        .path("course-requests/keepers/")
                        .queryParam("keeperIds", keeperIds)
                        .build()
                )
                .header("Authorization", authorizationHeaderContextHolder.getAuthorizationHeader())
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
