package org.example.person.service.implementations.courseregistration;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.person.dto.courserequest.CourseRegistrationRequestKeeperDto;
import org.example.person.exception.connect.ConnectException;
import org.example.person.exception.request.RequestNotFoundException;
import org.example.person.service.api.courseregistration.CourseRegistrationRequestKeeperService;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class CourseRegistrationRequestKeeperServiceImpl implements CourseRegistrationRequestKeeperService {
    private final WebClient.Builder webClientBuilder;

    @Override
    public List<CourseRegistrationRequestKeeperDto> findCourseRegistrationRequestKeepersByRequestId(String authorizationHeader, Long requestId) {
        return webClientBuilder
                .baseUrl("http://course-registration-service/api/v1/course-registration-app/").build()
                .get()
                .uri(uri -> uri
                        .path("course-requests/{requestId}/keepers/")
                        .build(requestId)
                )
                .header("Authorization", authorizationHeader)
                .retrieve()
                .onStatus(httpStatus -> httpStatus.isError() && !httpStatus.equals(HttpStatus.NOT_FOUND) && !httpStatus.equals(HttpStatus.UNAUTHORIZED), response -> {
                    log.error("failed to find course registration request keepers by request id {}", requestId);
                    throw new ConnectException();
                })
                .bodyToFlux(CourseRegistrationRequestKeeperDto.class)
                .timeout(Duration.ofSeconds(5))
                .onErrorResume(
                        WebClientResponseException.Unauthorized.class,
                        error -> Mono.error(
                                new AccessDeniedException(
                                        "Вам закрыт доступ к данной функциональности бортового компьютера"
                                )
                        )
                ).onErrorResume(
                        WebClientResponseException.NotFound.class,
                        error -> {
                            log.warn("request by id {} not found", requestId);
                            return Mono.error(
                                    new RequestNotFoundException(requestId)
                            );
                        }
                ).collectList()
                .block();
    }

    @Override
    public List<CourseRegistrationRequestKeeperDto> findProcessingCourseRegistrationRequestKeepersByKeeperIdIn(String authorizationHeader, List<Long> keeperIds) {
        return webClientBuilder
                .baseUrl("http://course-registration-service/api/v1/course-registration-app/").build()
                .get()
                .uri(uri -> uri
                        .path("course-requests/keepers/")
                        .queryParam("keeperIds", keeperIds)
                        .build()
                )
                .header("Authorization", authorizationHeader)
                .retrieve()
                .onStatus(httpStatus -> httpStatus.isError() && !httpStatus.equals(HttpStatus.UNAUTHORIZED), response -> {
                    log.error("failed to find processing course registration request keepers by keeper ids");
                    throw new ConnectException();
                })
                .bodyToFlux(CourseRegistrationRequestKeeperDto.class)
                .timeout(Duration.ofSeconds(5))
                .onErrorResume(
                        WebClientResponseException.Unauthorized.class,
                        error -> Mono.error(
                                new AccessDeniedException(
                                        "Вам закрыт доступ к данной функциональности бортового компьютера"
                                )
                        )
                ).collectList()
                .block();
    }
}
