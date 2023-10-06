package org.example.service.implementations;

import lombok.RequiredArgsConstructor;
import org.example.dto.explorer.ExplorerBaseInfoDto;
import org.example.dto.explorer.ExplorerDto;
import org.example.dto.explorer.ExplorerWithRatingDto;
import org.example.dto.person.PersonDto;
import org.example.exception.classes.connectEX.ConnectException;
import org.example.exception.classes.courseEX.CourseNotFoundException;
import org.example.repository.AuthorizationHeaderRepository;
import org.example.repository.PersonRepository;
import org.example.service.ExplorerService;
import org.example.service.RatingService;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Flux;

import java.time.Duration;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class ExplorerServiceImpl implements ExplorerService {
    private final WebClient.Builder webClientBuilder;
    private final AuthorizationHeaderRepository authorizationHeaderRepository;

    private final PersonRepository personRepository;

    private final RatingService ratingService;

    private final ModelMapper mapper;

    @Override
    public List<ExplorerWithRatingDto> getExplorersForCourse(Integer courseId) {
        List<ExplorerBaseInfoDto> explorers = findExplorersByCourseId(courseId);
        Map<Integer, Double> ratings = ratingService.getPeopleRatingAsExplorerByPersonIdIn(
                explorers.stream().map(ExplorerBaseInfoDto::getPersonId).collect(Collectors.toList())
        );
        return explorers.stream()
                .map(e -> {
                    ExplorerWithRatingDto explorer = mapper.map(e, ExplorerWithRatingDto.class);
                    explorer.setRating(ratings.get(e.getPersonId()));
                    return explorer;
                }).sorted()
                .collect(Collectors.toList());
    }

    private List<ExplorerBaseInfoDto> findExplorersByCourseId(Integer courseId) {
        List<ExplorerDto> explorers = webClientBuilder
                .baseUrl("http://explorer-service/api/v1/explorer-app/").build()
                .get()
                .uri(uri -> uri
                        .path("course/{courseId}/explorer/")
                        .build(courseId)
                )
                .header("Authorization", authorizationHeaderRepository.getAuthorizationHeader())
                .retrieve()
                .onStatus(httpStatus -> httpStatus.isError() && !httpStatus.equals(HttpStatus.NOT_FOUND), response -> {
                    throw new ConnectException();
                })
                .bodyToFlux(ExplorerDto.class)
                .timeout(Duration.ofSeconds(5))
                .onErrorResume(WebClientResponseException.NotFound.class, error -> Flux.error(new CourseNotFoundException(courseId)))
                .collectList()
                .block();
        if (explorers == null)
            return Collections.emptyList();
        Map<Integer, PersonDto> people = personRepository.findPeopleByPersonIdIn(
                explorers.stream().map(ExplorerDto::getPersonId).collect(Collectors.toList())
        );
        return explorers.stream()
                .map(e -> {
                    PersonDto currentKeeperPerson = people.get(e.getPersonId());
                    return new ExplorerBaseInfoDto(
                            currentKeeperPerson.getPersonId(),
                            currentKeeperPerson.getFirstName(),
                            currentKeeperPerson.getLastName(),
                            currentKeeperPerson.getPatronymic(),
                            e.getExplorerId()
                    );
                }).collect(Collectors.toList());
    }
}
