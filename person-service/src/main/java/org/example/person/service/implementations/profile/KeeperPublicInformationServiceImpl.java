package org.example.person.service.implementations.profile;

import org.example.person.dto.profile.KeeperPublicProfileDto;
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
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class KeeperPublicInformationServiceImpl extends PersonProfileInformationService implements KeeperPublicInformationService {
    private final ExplorerGroupRepository explorerGroupRepository;
    private final KeeperRepository keeperRepository;

    public KeeperPublicInformationServiceImpl(PersonService personService, RatingService ratingService, ExplorerGroupRepository explorerGroupRepository, KeeperRepository keeperRepository, FeedbackService feedbackService, CourseService courseService, ModelMapper mapper) {
        super(ratingService, courseService, feedbackService, personService, mapper);
        this.explorerGroupRepository = explorerGroupRepository;
        this.keeperRepository = keeperRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public KeeperPublicProfileDto getKeeperPublicInformation(String authorizationHeader, Long personId) {
        List<Keeper> keepers = keeperRepository.findKeepersByPersonId(personId);
        List<ExplorerGroup> explorerGroups = explorerGroupRepository.findExplorerGroupsByKeeperIdIn(
                keepers.stream().map(Keeper::getKeeperId).collect(Collectors.toList())
        );
        KeeperPublicProfileDto keeperPublicProfile = addPersonProfileInformation(
                new KeeperPublicProfileDto(), personId, keepers.size()
        );

        Flux.concat(
                        Mono.fromRunnable(() ->
                                keeperPublicProfile.setRating(ratingService.getPersonRatingAsKeeper(personId))
                        ), Mono.fromRunnable(() -> keeperPublicProfile.setTotalExplorers(
                                explorerGroups
                                        .stream()
                                        .mapToLong(g -> g.getExplorers().size())
                                        .sum()
                        )), Mono.fromRunnable(() -> keeperPublicProfile.setSystems(
                                courseService.getCoursesRating(
                                        authorizationHeader,
                                        keepers.stream().map(Keeper::getCourseId).collect(Collectors.toList())
                                ))
                        ), Mono.fromRunnable(() -> keeperPublicProfile.setFeedback(
                                feedbackService
                                        .getFeedbackForPersonAsKeeper(authorizationHeader, explorerGroups))
                        )
                ).parallel()
                .runOn(Schedulers.parallel())
                .then()
                .block();

        return keeperPublicProfile;
    }
}
