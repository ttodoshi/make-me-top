package org.example.person.service.implementations.profile;

import lombok.RequiredArgsConstructor;
import org.example.person.dto.person.GetPersonDto;
import org.example.person.model.Explorer;
import org.example.person.model.ExplorerGroup;
import org.example.person.model.Keeper;
import org.example.person.repository.ExplorerGroupRepository;
import org.example.person.repository.KeeperRepository;
import org.example.person.service.api.course.CourseService;
import org.example.person.service.api.feedback.FeedbackService;
import org.example.person.service.api.feedback.RatingService;
import org.example.person.service.api.profile.KeeperPublicInformationService;
import org.example.person.service.implementations.PersonService;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Mono;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class KeeperPublicInformationServiceImpl implements KeeperPublicInformationService {
    private final ExplorerGroupRepository explorerGroupRepository;
    private final KeeperRepository keeperRepository;

    private final PersonService personService;
    private final FeedbackService feedbackService;
    private final CourseService courseService;
    private final RatingService ratingService;

    private final ModelMapper mapper;

    @Override
    @Transactional(readOnly = true)
    public Map<String, Object> getKeeperPublicInformation(String authorizationHeader, Long personId) {
        Map<String, Object> response = new LinkedHashMap<>();
        response.put(
                "person",
                mapper.map(
                        personService.findPersonById(personId),
                        GetPersonDto.class
                )
        );
        response.put("rating", ratingService.getPersonRatingAsKeeper(personId));
        List<Keeper> keepers = keeperRepository.findKeepersByPersonId(personId);
        response.put("totalSystems", keepers.size());

        List<ExplorerGroup> explorerGroups = explorerGroupRepository.findExplorerGroupsByKeeperIdIn(
                keepers.stream().map(Keeper::getKeeperId).collect(Collectors.toList())
        );
        List<Explorer> explorers = explorerGroups
                .stream()
                .flatMap(g -> g.getExplorers().stream())
                .collect(Collectors.toList());
        response.put("totalExplorers", explorers.size());

        return Mono.when(
                Mono.fromRunnable(() -> response.put("systems", courseService.getCoursesRating(
                        authorizationHeader,
                        keepers.stream().map(Keeper::getCourseId).collect(Collectors.toList())
                ))), Mono.fromRunnable(() -> response.put("feedback", feedbackService
                        .getFeedbackForPersonAsKeeper(authorizationHeader, explorerGroups))
                )
        ).then(Mono.just(response)).block();
    }
}
