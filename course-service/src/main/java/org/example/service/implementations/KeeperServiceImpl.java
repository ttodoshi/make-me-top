package org.example.service.implementations;

import lombok.RequiredArgsConstructor;
import org.example.dto.keeper.KeeperBaseInfoDto;
import org.example.dto.keeper.KeeperDto;
import org.example.dto.keeper.KeeperWithRatingDto;
import org.example.dto.person.PersonDto;
import org.example.exception.classes.connectEX.ConnectException;
import org.example.exception.classes.courseEX.CourseNotFoundException;
import org.example.exception.classes.explorerEX.ExplorerNotFoundException;
import org.example.repository.AuthorizationHeaderRepository;
import org.example.repository.ExplorerGroupRepository;
import org.example.repository.ExplorerRepository;
import org.example.repository.PersonRepository;
import org.example.service.KeeperService;
import org.example.service.RatingService;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class KeeperServiceImpl implements KeeperService {
    private final WebClient.Builder webClientBuilder;
    private final AuthorizationHeaderRepository authorizationHeaderRepository;

    private final PersonRepository personRepository;
    private final ExplorerRepository explorerRepository;
    private final ExplorerGroupRepository explorerGroupRepository;
    private final RatingService ratingService;

    private final ModelMapper mapper;

    @Override
    public List<KeeperWithRatingDto> getKeepersForCourse(Integer courseId) {
        List<KeeperBaseInfoDto> keepers = findKeepersByCourseId(courseId);
        Map<Integer, Double> ratings = ratingService.getPeopleRatingAsKeeperByPersonIdIn(
                keepers.stream().map(KeeperBaseInfoDto::getPersonId).collect(Collectors.toList())
        );
        return keepers.stream()
                .map(k -> {
                    KeeperWithRatingDto keeper = mapper.map(k, KeeperWithRatingDto.class);
                    keeper.setRating(ratings.get(k.getPersonId()));
                    return keeper;
                }).sorted()
                .collect(Collectors.toList());
    }

    private List<KeeperBaseInfoDto> findKeepersByCourseId(Integer courseId) {
        List<KeeperDto> keepers = webClientBuilder
                .baseUrl("http://person-service/api/v1/person-app/").build()
                .get()
                .uri(uri -> uri
                        .path("course/{courseId}/keeper/")
                        .build(courseId)
                )
                .header("Authorization", authorizationHeaderRepository.getAuthorizationHeader())
                .retrieve()
                .onStatus(httpStatus -> httpStatus.isError() && !httpStatus.equals(HttpStatus.NOT_FOUND) && !httpStatus.equals(HttpStatus.UNAUTHORIZED), response -> {
                    throw new ConnectException();
                })
                .bodyToFlux(KeeperDto.class)
                .timeout(Duration.ofSeconds(5))
                .onErrorResume(WebClientResponseException.Unauthorized.class, error -> Mono.error(new AccessDeniedException("Вам закрыт доступ к данной функциональности бортового компьютера")))
                .onErrorResume(WebClientResponseException.NotFound.class, error -> Flux.error(new CourseNotFoundException(courseId)))
                .collectList()
                .block();
        if (keepers == null)
            return Collections.emptyList();
        Map<Integer, PersonDto> people = personRepository.findPeopleByPersonIdIn(
                keepers.stream().map(KeeperDto::getPersonId).collect(Collectors.toList())
        );
        return keepers.stream()
                .map(k -> {
                    PersonDto currentKeeperPerson = people.get(k.getPersonId());
                    return new KeeperBaseInfoDto(
                            currentKeeperPerson.getPersonId(),
                            currentKeeperPerson.getFirstName(),
                            currentKeeperPerson.getLastName(),
                            currentKeeperPerson.getPatronymic(),
                            k.getKeeperId()
                    );
                }).collect(Collectors.toList());
    }

    @Override
    public Optional<KeeperWithRatingDto> getKeeperForExplorer(Integer explorerId, List<KeeperWithRatingDto> keepers) {
        Integer explorersKeeperId = explorerGroupRepository.getReferenceById(
                explorerRepository.findById(explorerId)
                        .orElseThrow(ExplorerNotFoundException::new)
                        .getGroupId()
        ).getKeeperId();
        return keepers.stream()
                .filter(k -> k.getKeeperId().equals(explorersKeeperId))
                .findAny();
    }
}
