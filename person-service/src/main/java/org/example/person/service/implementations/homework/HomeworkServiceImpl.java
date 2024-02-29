package org.example.person.service.implementations.homework;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.person.dto.course.CourseDto;
import org.example.person.dto.homework.GetHomeworkRequestDto;
import org.example.person.dto.homework.GetHomeworkWithMarkDto;
import org.example.person.dto.homework.HomeworkDto;
import org.example.person.dto.homework.HomeworkRequestDto;
import org.example.person.dto.planet.PlanetDto;
import org.example.person.exception.connect.ConnectException;
import org.example.person.model.Explorer;
import org.example.person.model.ExplorerGroup;
import org.example.person.model.Person;
import org.example.person.repository.ExplorerGroupRepository;
import org.example.person.repository.ExplorerRepository;
import org.example.person.service.api.course.CourseService;
import org.example.person.service.api.homework.HomeworkRequestService;
import org.example.person.service.api.homework.HomeworkService;
import org.example.person.service.api.planet.PlanetService;
import org.example.person.service.implementations.ExplorerService;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class HomeworkServiceImpl implements HomeworkService {
    private final ExplorerRepository explorerRepository;
    private final ExplorerGroupRepository explorerGroupRepository;

    private final ExplorerService explorerService;
    private final HomeworkRequestService homeworkRequestService;
    private final CourseService courseService;
    private final PlanetService planetService;

    private final WebClient.Builder webClientBuilder;


    @Override
    public Map<Long, HomeworkDto> findHomeworksByHomeworkIdIn(String authorizationHeader, List<Long> homeworkIds) {
        return webClientBuilder
                .baseUrl("http://homework-service/api/v1/homework-app/").build()
                .get()
                .uri(uri -> uri
                        .path("homeworks/")
                        .queryParam("homeworkIds", homeworkIds)
                        .build()
                )
                .header("Authorization", authorizationHeader)
                .retrieve()
                .onStatus(httpStatus -> httpStatus.isError() && !httpStatus.equals(HttpStatus.UNAUTHORIZED), response -> {
                    log.error("failed to find homeworks by homework ids");
                    throw new ConnectException();
                })
                .bodyToFlux(new ParameterizedTypeReference<Map<Long, HomeworkDto>>() {
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
    public List<GetHomeworkWithMarkDto> findHomeworksByCourseThemeId(String authorizationHeader, Long themeId) {
        return webClientBuilder
                .baseUrl("http://homework-service/api/v1/homework-app/").build()
                .get()
                .uri(uri -> uri
                        .path("themes/{themeId}/homeworks/")
                        .build(themeId)
                )
                .header("Authorization", authorizationHeader)
                .retrieve()
                .onStatus(httpStatus -> httpStatus.isError() && !httpStatus.equals(HttpStatus.UNAUTHORIZED), response -> {
                    log.error("failed to find homeworks by theme id {}", themeId);
                    throw new ConnectException();
                })
                .bodyToFlux(GetHomeworkWithMarkDto.class)
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
    @Transactional(readOnly = true)
    public List<GetHomeworkRequestDto> getHomeworkRequestsFromExplorersByGroups(String authorizationHeader, Map<Long, ExplorerGroup> explorerGroups) {
        Map<Long, Explorer> explorers = explorerGroups.values()
                .stream()
                .flatMap(g -> g.getExplorers().stream())
                .collect(Collectors.toMap(Explorer::getExplorerId, e -> e));
        Map<Long, CourseDto> courses = courseService.findCoursesByCourseIdIn(
                authorizationHeader,
                explorerGroups.values().stream().map(ExplorerGroup::getCourseId).collect(Collectors.toList())
        );

        List<HomeworkRequestDto> homeworkRequests = homeworkRequestService.findOpenedHomeworkRequestsByExplorerIdIn(
                authorizationHeader,
                explorers.values().stream().map(Explorer::getExplorerId).collect(Collectors.toList())
        );
        Map<Long, HomeworkDto> homeworks = findHomeworksByHomeworkIdIn(
                authorizationHeader,
                homeworkRequests.stream().map(HomeworkRequestDto::getHomeworkId).collect(Collectors.toList())
        );

        Map<Long, PlanetDto> planets = planetService.findPlanetsByPlanetIdIn(
                authorizationHeader,
                homeworks.values().stream().map(HomeworkDto::getCourseThemeId).collect(Collectors.toList())
        );
        return homeworkRequests.stream()
                .map(hr -> {
                    Explorer requestExplorer = explorers.get(hr.getExplorerId());
                    Person person = requestExplorer.getPerson();
                    CourseDto requestCourse = courses.get(
                            explorerGroups.get(
                                    requestExplorer.getGroupId()
                            ).getCourseId()
                    );
                    PlanetDto requestPlanet = planets.get(
                            homeworks.get(hr.getHomeworkId()).getCourseThemeId()
                    );
                    return new GetHomeworkRequestDto(
                            hr.getRequestId(),
                            person.getPersonId(), person.getFirstName(),
                            person.getLastName(), person.getPatronymic(),
                            requestCourse.getCourseId(), requestCourse.getTitle(),
                            hr.getExplorerId(),
                            requestPlanet.getPlanetId(), requestPlanet.getPlanetName(),
                            hr.getHomeworkId(), hr.getStatus()
                    );
                }).collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<GetHomeworkRequestDto> getHomeworkRequestsFromPerson(String authorizationHeader, List<Explorer> personExplorers) {
        List<HomeworkRequestDto> openedHomeworkRequests = homeworkRequestService.findOpenedHomeworkRequestsByExplorerIdIn(
                authorizationHeader,
                personExplorers.stream().map(Explorer::getExplorerId).collect(Collectors.toList())
        );
        Map<Long, HomeworkDto> homeworks = findHomeworksByHomeworkIdIn(
                authorizationHeader,
                openedHomeworkRequests.stream().map(HomeworkRequestDto::getHomeworkId).collect(Collectors.toList())
        );

        Map<Long, PlanetDto> planets = planetService.findPlanetsByPlanetIdIn(
                authorizationHeader,
                homeworks.values()
                        .stream()
                        .map(HomeworkDto::getCourseThemeId)
                        .collect(Collectors.toList())
        );
        Map<Long, CourseDto> courses = courseService.findCoursesByCourseIdIn(
                authorizationHeader,
                planets.values()
                        .stream()
                        .map(PlanetDto::getSystemId)
                        .collect(Collectors.toList())
        );

        return openedHomeworkRequests
                .stream()
                .map(hr -> {
                    Explorer requestExplorer = explorerRepository.getReferenceById(hr.getExplorerId());
                    Person person = requestExplorer.getPerson();
                    CourseDto requestCourse = courses.get(
                            requestExplorer.getGroup().getCourseId()
                    );
                    PlanetDto requestPlanet = planets.get(
                            homeworks.get(hr.getHomeworkId()).getCourseThemeId()
                    );
                    return new GetHomeworkRequestDto(
                            hr.getRequestId(),
                            person.getPersonId(), person.getFirstName(),
                            person.getLastName(), person.getPatronymic(),
                            requestCourse.getCourseId(), requestCourse.getTitle(),
                            hr.getExplorerId(),
                            requestPlanet.getPlanetId(), requestPlanet.getPlanetName(),
                            hr.getHomeworkId(), hr.getStatus()
                    );
                }).collect(Collectors.toList());
    }
}
