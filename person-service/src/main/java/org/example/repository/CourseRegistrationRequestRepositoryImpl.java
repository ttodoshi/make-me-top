package org.example.repository;

import lombok.RequiredArgsConstructor;
import org.example.dto.courserequest.ApprovedRequestDto;
import org.example.dto.courserequest.CourseRegistrationRequestDto;
import org.example.exception.classes.connectEX.ConnectException;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class CourseRegistrationRequestRepositoryImpl implements CourseRegistrationRequestRepository {
    private final WebClient.Builder webClientBuilder;
    private final AuthorizationHeaderRepository authorizationHeaderRepository;

    @Override
    public Optional<CourseRegistrationRequestDto> findProcessingCourseRegistrationRequestByPersonId() {
        return webClientBuilder
                .baseUrl("http://course-registration-service/api/v1/course-registration-app/").build()
                .get()
                .uri("course-requests/processing/")
                .header("Authorization", authorizationHeaderRepository.getAuthorizationHeader())
                .retrieve()
                .onStatus(httpStatus -> httpStatus.isError() && !httpStatus.equals(HttpStatus.NOT_FOUND) && !httpStatus.equals(HttpStatus.UNAUTHORIZED), response -> {
                    throw new ConnectException();
                })
                .bodyToMono(CourseRegistrationRequestDto.class)
                .timeout(Duration.ofSeconds(5))
                .onErrorResume(WebClientResponseException.Unauthorized.class, error -> Mono.error(new AccessDeniedException("Вам закрыт доступ к данной функциональности бортового компьютера")))
                .onErrorResume(WebClientResponseException.NotFound.class, error -> Mono.empty())
                .blockOptional();
    }

    @Override
    public Map<Integer, CourseRegistrationRequestDto> findCourseRegistrationRequestsByRequestIdIn(List<Integer> requestIds) {
        return webClientBuilder
                .baseUrl("http://course-registration-service/api/v1/course-registration-app/").build()
                .get()
                .uri(uri -> uri
                        .path("course-requests/")
                        .queryParam("requestIds", requestIds)
                        .build()
                )
                .header("Authorization", authorizationHeaderRepository.getAuthorizationHeader())
                .retrieve()
                .onStatus(httpStatus -> httpStatus.isError() && !httpStatus.equals(HttpStatus.UNAUTHORIZED), response -> {
                    throw new ConnectException();
                })
                .bodyToFlux(new ParameterizedTypeReference<Map<Integer, CourseRegistrationRequestDto>>() {
                })
                .timeout(Duration.ofSeconds(5))
                .onErrorResume(WebClientResponseException.Unauthorized.class, error -> Mono.error(new AccessDeniedException("Вам закрыт доступ к данной функциональности бортового компьютера")))
                .blockLast();
    }

    @Override
    public List<ApprovedRequestDto> getApprovedCourseRegistrationRequests(List<Integer> keeperIds) {
        return webClientBuilder
                .baseUrl("http://course-registration-service/api/v1/course-registration-app/").build()
                .get()
                .uri(uri -> uri
                        .path("course-requests/approved/")
                        .queryParam("keeperIds", keeperIds)
                        .build()
                )
                .header("Authorization", authorizationHeaderRepository.getAuthorizationHeader())
                .retrieve()
                .onStatus(httpStatus -> httpStatus.isError() && !httpStatus.equals(HttpStatus.UNAUTHORIZED), response -> {
                    throw new ConnectException();
                })
                .bodyToFlux(ApprovedRequestDto.class)
                .timeout(Duration.ofSeconds(5))
                .onErrorResume(WebClientResponseException.Unauthorized.class, error -> Mono.error(new AccessDeniedException("Вам закрыт доступ к данной функциональности бортового компьютера")))
                .collectList()
                .block();
    }
}
