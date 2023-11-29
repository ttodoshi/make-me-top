package org.example.course.service.implementations;

import lombok.RequiredArgsConstructor;
import org.example.course.dto.keeper.KeeperBaseInfoDto;
import org.example.course.dto.keeper.KeeperWithRatingDto;
import org.example.course.exception.classes.explorer.ExplorerNotFoundException;
import org.example.course.exception.classes.keeper.KeeperNotFoundException;
import org.example.course.repository.ExplorerGroupRepository;
import org.example.course.repository.ExplorerRepository;
import org.example.course.repository.KeeperRepository;
import org.example.course.repository.PersonRepository;
import org.example.course.service.KeeperService;
import org.example.course.service.RatingService;
import org.example.grpc.KeepersService;
import org.example.grpc.PeopleService;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class KeeperServiceImpl implements KeeperService {
    private final PersonRepository personRepository;
    private final ExplorerRepository explorerRepository;
    private final ExplorerGroupRepository explorerGroupRepository;
    private final KeeperRepository keeperRepository;
    private final RatingService ratingService;

    private final ModelMapper mapper;

    @Override
    public List<KeeperWithRatingDto> getKeepersForCourse(Long courseId) {
        List<KeeperBaseInfoDto> keepers = getKeepersFromCourse(courseId);
        Map<Long, Double> ratings = ratingService.getPeopleRatingAsKeeperByPersonIdIn(
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

    private List<KeeperBaseInfoDto> getKeepersFromCourse(Long courseId) {
        List<KeepersService.Keeper> keepers = keeperRepository.findKeepersByCourseId(courseId);
        Map<Long, PeopleService.Person> people = personRepository.findPeopleByPersonIdIn(
                keepers.stream().map(KeepersService.Keeper::getPersonId).collect(Collectors.toList())
        );
        return keepers.stream()
                .map(k -> {
                    PeopleService.Person currentKeeperPerson = people.get(k.getPersonId());
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
    public KeeperWithRatingDto getKeeperForExplorer(Long explorerId, List<KeeperWithRatingDto> keepers) {
        Long explorersKeeperId = explorerGroupRepository.getReferenceById(
                explorerRepository.findById(explorerId)
                        .orElseThrow(ExplorerNotFoundException::new)
                        .getGroupId()
        ).getKeeperId();
        return keepers.stream()
                .filter(k -> k.getKeeperId().equals(explorersKeeperId))
                .findAny()
                .orElseThrow(KeeperNotFoundException::new);
    }
}
