package org.example.person.service.implementations.courseregistration;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.person.dto.course.CourseDto;
import org.example.person.dto.courserequest.*;
import org.example.person.dto.galaxy.GalaxyDto;
import org.example.person.dto.keeper.KeeperBasicInfoDto;
import org.example.person.exception.connect.ConnectException;
import org.example.person.model.Keeper;
import org.example.person.model.Person;
import org.example.person.service.api.course.CourseService;
import org.example.person.service.api.courseregistration.CourseRegistrationRequestKeeperService;
import org.example.person.service.api.courseregistration.CourseRegistrationRequestService;
import org.example.person.service.api.feedback.RatingService;
import org.example.person.service.api.galaxy.GalaxyService;
import org.example.person.service.implementations.KeeperService;
import org.example.person.service.implementations.PersonService;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class CourseRegistrationRequestServiceImpl implements CourseRegistrationRequestService {
    private final CourseService courseService;
    private final CourseRegistrationRequestKeeperService courseRegistrationRequestKeeperService;
    private final GalaxyService galaxyService;
    private final PersonService personService;
    private final KeeperService keeperService;
    private final RatingService ratingService;

    private final WebClient.Builder webClientBuilder;

    @Override
    public Optional<CourseRegistrationRequestDto> findProcessingCourseRegistrationRequest(String authorizationHeader) {
        return webClientBuilder
                .baseUrl("http://course-registration-service/api/v1/course-registration-app/").build()
                .get()
                .uri("course-requests/processing/")
                .header("Authorization", authorizationHeader)
                .retrieve()
                .onStatus(httpStatus -> httpStatus.isError() && !httpStatus.equals(HttpStatus.NOT_FOUND) && !httpStatus.equals(HttpStatus.UNAUTHORIZED), response -> {
                    log.error("failed to find processing course registration request");
                    throw new ConnectException();
                })
                .bodyToMono(CourseRegistrationRequestDto.class)
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
                        error -> Mono.empty()
                ).blockOptional();
    }

    @Override
    public Map<Long, CourseRegistrationRequestDto> findCourseRegistrationRequestsByRequestIdIn(String authorizationHeader, List<Long> requestIds) {
        return webClientBuilder
                .baseUrl("http://course-registration-service/api/v1/course-registration-app/").build()
                .get()
                .uri(uri -> uri
                        .path("course-requests/")
                        .queryParam("requestIds", requestIds)
                        .build()
                )
                .header("Authorization", authorizationHeader)
                .retrieve()
                .onStatus(httpStatus -> httpStatus.isError() && !httpStatus.equals(HttpStatus.UNAUTHORIZED), response -> {
                    log.error("failed to find course registration requests by request ids");
                    throw new ConnectException();
                })
                .bodyToFlux(new ParameterizedTypeReference<Map<Long, CourseRegistrationRequestDto>>() {
                })
                .timeout(Duration.ofSeconds(5))
                .onErrorResume(
                        WebClientResponseException.Unauthorized.class,
                        error -> Mono.error(
                                new AccessDeniedException(
                                        "Вам закрыт доступ к данной функциональности бортового компьютера"
                                )
                        )
                ).blockLast();
    }

    @Override
    public List<ApprovedRequestDto> getApprovedCourseRegistrationRequests(String authorizationHeader, List<Long> keeperIds) {
        return webClientBuilder
                .baseUrl("http://course-registration-service/api/v1/course-registration-app/").build()
                .get()
                .uri(uri -> uri
                        .path("course-requests/approved/")
                        .queryParam("keeperIds", keeperIds)
                        .build()
                )
                .header("Authorization", authorizationHeader)
                .retrieve()
                .onStatus(httpStatus -> httpStatus.isError() && !httpStatus.equals(HttpStatus.UNAUTHORIZED), response -> {
                    log.error("failed to get approved course registration requests by keeper ids");
                    throw new ConnectException();
                })
                .bodyToFlux(ApprovedRequestDto.class)
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

    @Override
    public List<CourseRegistrationRequestsForKeeperDto> getStudyRequestsForKeeper(String authorizationHeader, List<Keeper> keepers) {
        List<CourseRegistrationRequestKeeperDto> openedRequests = courseRegistrationRequestKeeperService
                .findProcessingCourseRegistrationRequestKeepersByKeeperIdIn(
                        authorizationHeader,
                        keepers.stream().map(Keeper::getKeeperId).collect(Collectors.toList())
                );
        Map<Long, CourseRegistrationRequestDto> requests = findCourseRegistrationRequestsByRequestIdIn(
                authorizationHeader,
                openedRequests.stream().map(CourseRegistrationRequestKeeperDto::getRequestId).collect(Collectors.toList())
        );

        Map<Long, CourseDto> courses = courseService.findCoursesByCourseIdIn(
                authorizationHeader,
                requests.values().stream().map(CourseRegistrationRequestDto::getCourseId).collect(Collectors.toList())
        );
        Map<Long, Double> ratings = ratingService.getPeopleRatingAsExplorerByPersonIdIn(
                requests.values().stream().map(CourseRegistrationRequestDto::getPersonId).collect(Collectors.toList())
        );

        return openedRequests.stream()
                .map(kr -> {
                    CourseRegistrationRequestDto currentRequest = requests.get(kr.getRequestId());
                    Person person = personService.findPersonById(currentRequest.getPersonId());
                    return new CourseRegistrationRequestsForKeeperDto.CourseRegistrationRequestForKeeperDto(
                            person.getPersonId(),
                            person.getFirstName(),
                            person.getLastName(),
                            person.getPatronymic(),
                            currentRequest.getCourseId(),
                            courses.get(currentRequest.getCourseId()).getTitle(),
                            currentRequest.getRequestId(),
                            currentRequest.getRequestDate(),
                            kr.getKeeperId(),
                            ratings.get(person.getPersonId())
                    );
                }).collect(Collectors.groupingBy(
                        r -> Map.entry(r.getCourseId(), r.getCourseTitle()),
                        Collectors.mapping(Function.identity(), Collectors.toList())
                )).entrySet().stream()
                .map(e -> {
                    Collections.sort(e.getValue());
                    return new CourseRegistrationRequestsForKeeperDto(
                            e.getKey().getKey(),
                            e.getKey().getValue(),
                            e.getValue()
                    );
                }).collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<CourseRegistrationRequestForExplorerDto> getStudyRequestForExplorerByPersonId(String authorizationHeader) {
        return findProcessingCourseRegistrationRequest(authorizationHeader)
                .map(r -> {
                    CourseDto course = courseService.findCourseById(authorizationHeader, r.getCourseId());
                    GalaxyDto galaxy = galaxyService.findGalaxyBySystemId(authorizationHeader, r.getCourseId());

                    List<Long> keeperIds = courseRegistrationRequestKeeperService
                            .findCourseRegistrationRequestKeepersByRequestId(authorizationHeader, r.getRequestId())
                            .stream()
                            .map(CourseRegistrationRequestKeeperDto::getKeeperId)
                            .collect(Collectors.toList());
                    List<KeeperBasicInfoDto> keepers = keeperService
                            .findKeepersByKeeperIdIn(keeperIds)
                            .entrySet()
                            .stream()
                            .map(e -> {
                                Person person = e.getValue().getPerson();
                                return new KeeperBasicInfoDto(
                                        person.getPersonId(), person.getFirstName(),
                                        person.getLastName(), person.getPatronymic(), e.getKey()
                                );
                            }).collect(Collectors.toList());

                    return new CourseRegistrationRequestForExplorerDto(
                            r.getRequestId(), r.getCourseId(), course.getTitle(),
                            galaxy.getGalaxyId(), galaxy.getGalaxyName(), keepers
                    );
                });
    }

    @Override
    public Optional<CourseRegistrationRequestForKeeperWithGalaxyDto> getStudyRequesForKeepertByExplorerPersonId(String authorizationHeader, Long keeperPersonId, Long personId) {
        // returns information about the request only if the authorized keeper is the one to whom it was sent
        return getStudyRequestsForKeeper(
                authorizationHeader,
                keeperService.findKeepersByPersonId(keeperPersonId)
        ).stream()
                .flatMap(requests -> requests.getRequests().stream())
                .filter(r -> r.getPersonId().equals(personId))
                .findAny()
                .map(r -> {
                    GalaxyDto galaxy = galaxyService.findGalaxyBySystemId(authorizationHeader, r.getCourseId());
                    return new CourseRegistrationRequestForKeeperWithGalaxyDto(
                            r.getRequestId(), r.getPersonId(), r.getFirstName(),
                            r.getLastName(), r.getPatronymic(), r.getCourseId(), r.getCourseTitle(),
                            ratingService.getPersonRatingAsExplorer(personId),
                            galaxy.getGalaxyId(), galaxy.getGalaxyName()
                    );
                });
    }

    @Override
    @Transactional(readOnly = true)
    public List<GetApprovedCourseRegistrationRequestsForKeeperDto> getApprovedRequestsForKeeper(String authorizationHeader, List<Keeper> keepers) {
        List<ApprovedRequestDto> approvedRequests =
                getApprovedCourseRegistrationRequests(
                        authorizationHeader,
                        keepers.stream().map(Keeper::getKeeperId).collect(Collectors.toList())
                );

        Map<Long, CourseDto> courses = courseService.findCoursesByCourseIdIn(
                authorizationHeader,
                approvedRequests.stream().map(ApprovedRequestDto::getCourseId).collect(Collectors.toList())
        );
        Map<Long, Double> ratings = ratingService.getPeopleRatingAsExplorerByPersonIdIn(
                approvedRequests.stream().map(ApprovedRequestDto::getPersonId).collect(Collectors.toList())
        );

        return approvedRequests.stream()
                .map(r -> {
                    Person person = personService.findPersonById(r.getPersonId());
                    return new GetApprovedCourseRegistrationRequestsForKeeperDto.ApprovedCourseRegistrationRequestDto(
                            person.getPersonId(), person.getFirstName(), person.getLastName(), person.getPatronymic(),
                            r.getCourseId(), courses.get(r.getCourseId()).getTitle(),
                            r.getRequestId(), r.getResponseDate(), r.getKeeperId(),
                            ratings.get(person.getPersonId())
                    );
                }).collect(Collectors.groupingBy(
                        r -> new AbstractMap.SimpleEntry<>(r.getCourseId(), r.getCourseTitle()),
                        Collectors.mapping(Function.identity(), Collectors.toList())
                )).entrySet().stream()
                .map(e -> {
                    Collections.sort(e.getValue());
                    return new GetApprovedCourseRegistrationRequestsForKeeperDto(
                            e.getKey().getKey(),
                            e.getKey().getValue(),
                            e.getValue()
                    );
                }).collect(Collectors.toList());
    }
}
