package org.example.person.service.implementations.profile;

import lombok.RequiredArgsConstructor;
import org.example.person.dto.galaxy.GalaxyDto;
import org.example.person.dto.person.PersonWithGalaxiesDto;
import org.example.person.model.Keeper;
import org.example.person.model.Person;
import org.example.person.repository.PersonRepository;
import org.example.person.service.api.feedback.RatingService;
import org.example.person.service.api.galaxy.GalaxyService;
import org.example.person.service.api.profile.KeeperListService;
import org.example.person.service.implementations.KeeperService;
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
public class KeeperListServiceImpl implements KeeperListService {
    private final PersonRepository personRepository;
    private final GalaxyService galaxyService;

    private final KeeperService keeperService;
    private final RatingService ratingService;


    @Override
    @Transactional(readOnly = true)
    public Page<PersonWithGalaxiesDto> getKeepers(String authorizationHeader, Integer page, Integer size) {
        Page<Person> peoplePage = personRepository.findKeeperPeople(
                PageRequest.of(page, size)
        );
        Map<Long, Double> ratings = ratingService.getPeopleRatingAsExplorerByPersonIdIn(
                peoplePage.stream()
                        .map(Person::getPersonId)
                        .collect(Collectors.toList())
        );

        Map<Long, List<Keeper>> keepers = keeperService.findKeepersByPersonIdIn(
                peoplePage.stream()
                        .map(Person::getPersonId)
                        .collect(Collectors.toList())
        );
        Map<Long, GalaxyDto> galaxyMap = galaxyService.findGalaxiesBySystemIdIn(
                authorizationHeader,
                keepers.entrySet()
                        .stream()
                        .flatMap(e -> e.getValue().stream())
                        .map(Keeper::getCourseId)
                        .distinct()
                        .collect(Collectors.toList())
        );

        return peoplePage.map(
                p -> new PersonWithGalaxiesDto(
                        p.getPersonId(),
                        p.getFirstName(),
                        p.getLastName(),
                        p.getPatronymic(),
                        ratings.getOrDefault(p.getPersonId(), 0.0),
                        keepers.getOrDefault(p.getPersonId(), Collections.emptyList())
                                .stream()
                                .map(k -> galaxyMap.get(k.getCourseId()))
                                .distinct()
                                .collect(Collectors.toList())
                )
        );
    }
}
