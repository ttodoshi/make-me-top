package org.example.course.service.implementations;

import lombok.RequiredArgsConstructor;
import org.example.course.dto.explorer.ExplorerWithRatingDto;
import org.example.course.repository.ExplorerRepository;
import org.example.course.repository.PersonRepository;
import org.example.course.service.ExplorerService;
import org.example.course.service.RatingService;
import org.example.grpc.ExplorersService;
import org.example.grpc.PeopleService;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

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
    public List<ExplorerWithRatingDto> getExplorersForCourse(Long courseId) {
        List<ExplorersService.Explorer> explorers = explorerRepository.findExplorersByCourseId(courseId);
        Map<Long, PeopleService.Person> people = personRepository.findPeopleByPersonIdIn(
                explorers.stream().map(ExplorersService.Explorer::getPersonId).collect(Collectors.toList())
        );
        Map<Long, Double> ratings = ratingService.getPeopleRatingAsExplorerByPersonIdIn(
                explorers.stream().map(ExplorersService.Explorer::getPersonId).collect(Collectors.toList())
        );
        return explorers.stream()
                .map(e -> {
                    PeopleService.Person currentExplorerPerson = people.get(e.getPersonId());
                    return new ExplorerWithRatingDto(
                            currentExplorerPerson.getPersonId(),
                            currentExplorerPerson.getFirstName(),
                            currentExplorerPerson.getLastName(),
                            currentExplorerPerson.getPatronymic(),
                            e.getExplorerId(),
                            ratings.get(e.getPersonId())
                    );
                }).sorted()
                .collect(Collectors.toList());
    }
}
