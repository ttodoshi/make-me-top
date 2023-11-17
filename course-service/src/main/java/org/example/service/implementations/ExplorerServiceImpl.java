package org.example.service.implementations;

import lombok.RequiredArgsConstructor;
import org.example.dto.explorer.ExplorerBaseInfoDto;
import org.example.dto.explorer.ExplorerWithRatingDto;
import org.example.grpc.ExplorersService;
import org.example.grpc.PeopleService;
import org.example.repository.AuthorizationHeaderRepository;
import org.example.repository.ExplorerRepository;
import org.example.repository.PersonRepository;
import org.example.service.ExplorerService;
import org.example.service.RatingService;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class ExplorerServiceImpl implements ExplorerService {
    private final PersonRepository personRepository;
    private final ExplorerRepository explorerRepository;

    private final RatingService ratingService;

    private final ModelMapper mapper;

    @Override
    public List<ExplorerWithRatingDto> getExplorersForCourse(Integer courseId) {
        List<ExplorerBaseInfoDto> explorers = getExplorersFromCourse(courseId);
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

    private List<ExplorerBaseInfoDto> getExplorersFromCourse(Integer courseId) {
        List<ExplorersService.Explorer> explorers = explorerRepository.findExplorersByCourseId(courseId);
        Map<Integer, PeopleService.Person> people = personRepository.findPeopleByPersonIdIn(
                explorers.stream().map(ExplorersService.Explorer::getPersonId).collect(Collectors.toList())
        );
        return explorers.stream()
                .map(e -> {
                    PeopleService.Person currentKeeperPerson = people.get(e.getPersonId());
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
