package org.example.person.service.implementations.profile;

import lombok.RequiredArgsConstructor;
import org.example.person.dto.galaxy.GalaxyDto;
import org.example.person.dto.person.PersonWithGalaxiesDto;
import org.example.person.model.Explorer;
import org.example.person.model.Person;
import org.example.person.repository.PersonRepository;
import org.example.person.service.api.feedback.RatingService;
import org.example.person.service.api.galaxy.GalaxyService;
import org.example.person.service.api.profile.ExplorerListService;
import org.example.person.service.implementations.ExplorerService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ExplorerListServiceImpl implements ExplorerListService {
    private final PersonRepository personRepository;
    private final GalaxyService galaxyService;

    private final ExplorerService explorerService;
    private final RatingService ratingService;

    @Override
    @Transactional(readOnly = true)
    public Page<PersonWithGalaxiesDto> getExplorers(String authorizationHeader, Integer page, Integer size) {
        Page<Person> peoplePage = personRepository.findExplorerPeople(
                PageRequest.of(page, size)
        );
        Map<Long, Double> ratings = ratingService.getPeopleRatingAsExplorerByPersonIdIn(
                peoplePage.stream()
                        .map(Person::getPersonId)
                        .collect(Collectors.toList())
        );
        Map<Long, List<Explorer>> explorers = explorerService.findExplorersByPersonIdIn(
                peoplePage.stream()
                        .map(Person::getPersonId)
                        .collect(Collectors.toList())
        );
        Map<Long, GalaxyDto> galaxyMap = findExplorersGalaxies(authorizationHeader, explorers);

        return peoplePage.map(
                p -> new PersonWithGalaxiesDto(
                        p.getPersonId(), p.getFirstName(), p.getLastName(), p.getPatronymic(),
                        ratings.getOrDefault(p.getPersonId(), 0.0),
                        explorers.getOrDefault(p.getPersonId(), Collections.emptyList())
                                .stream()
                                .map(e -> galaxyMap.get(e.getGroup().getCourseId()))
                                .distinct()
                                .collect(Collectors.toList())
                )
        );
    }

    private Map<Long, GalaxyDto> findExplorersGalaxies(String authorizationHeader, Map<Long, List<Explorer>> explorers) {
        return galaxyService.findGalaxiesBySystemIdIn(
                authorizationHeader,
                explorers.entrySet()
                        .stream()
                        .flatMap(e -> e.getValue().stream())
                        .map(e -> e.getGroup().getCourseId())
                        .distinct()
                        .collect(Collectors.toList())
        );
    }
}
