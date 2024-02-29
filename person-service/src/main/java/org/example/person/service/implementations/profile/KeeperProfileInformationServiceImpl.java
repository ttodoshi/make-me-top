package org.example.person.service.implementations.profile;

import org.example.person.dto.profile.KeeperProfileDto;
import org.example.person.model.Explorer;
import org.example.person.model.ExplorerGroup;
import org.example.person.model.Keeper;
import org.example.person.repository.ExplorerGroupRepository;
import org.example.person.repository.KeeperRepository;
import org.example.person.service.api.course.CourseService;
import org.example.person.service.api.courseregistration.CourseRegistrationRequestService;
import org.example.person.service.api.feedback.FeedbackService;
import org.example.person.service.api.feedback.RatingService;
import org.example.person.service.api.homework.HomeworkService;
import org.example.person.service.api.profile.KeeperProfileInformationService;
import org.example.person.service.api.progress.CourseProgressService;
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
public class KeeperProfileInformationServiceImpl extends PersonProfileInformationService implements KeeperProfileInformationService {
    private final ExplorerGroupRepository explorerGroupRepository;
    private final KeeperRepository keeperRepository;

    private final HomeworkService homeworkService;
    private final CourseRegistrationRequestService courseRegistrationRequestService;
    private final CourseProgressService courseProgressService;

    public KeeperProfileInformationServiceImpl(PersonService personService, RatingService ratingService, ExplorerGroupRepository explorerGroupRepository, KeeperRepository keeperRepository, FeedbackService feedbackService, HomeworkService homeworkService, CourseRegistrationRequestService courseRegistrationRequestService, CourseProgressService courseProgressService, CourseService courseService, ModelMapper mapper) {
        super(ratingService, courseService, feedbackService, personService, mapper);
        this.explorerGroupRepository = explorerGroupRepository;
        this.keeperRepository = keeperRepository;
        this.homeworkService = homeworkService;
        this.courseRegistrationRequestService = courseRegistrationRequestService;
        this.courseProgressService = courseProgressService;
    }

    @Override
    @Transactional(readOnly = true)
    public KeeperProfileDto getKeeperProfileInformation(String authorizationHeader, Long authenticatedPersonId) {
        List<Keeper> keepers = keeperRepository.findKeepersByPersonId(authenticatedPersonId);
        List<ExplorerGroup> explorerGroups = explorerGroupRepository.findExplorerGroupsByKeeperIdIn(
                keepers.stream().map(Keeper::getKeeperId).collect(Collectors.toList())
        );
        KeeperProfileDto keeperProfile = addPersonProfileInformation(
                new KeeperProfileDto(), authenticatedPersonId, keepers.size()
        );

        Flux.concat(
                        Mono.fromRunnable(() ->
                                keeperProfile.setRating(ratingService.getPersonRatingAsKeeper(authenticatedPersonId))
                        ), Mono.fromRunnable(() -> keeperProfile.setTotalExplorers(
                                explorerGroups.stream().mapToLong(g -> g.getExplorers().size()).sum())
                        ), Mono.fromRunnable(() -> keeperProfile.setSystems(
                                courseService.getCoursesRating(
                                        authorizationHeader,
                                        keepers.stream().map(Keeper::getCourseId).collect(Collectors.toList())
                                ))
                        ), Mono.fromRunnable(() -> courseProgressService
                                .getCurrentGroup(authorizationHeader, explorerGroups)
                                .ifPresent(keeperProfile::setCurrentGroup)
                        ), Mono.fromRunnable(() -> keeperProfile.setKeeperFeedbacks(
                                feedbackService.getKeeperFeedbackOffers(
                                        authorizationHeader,
                                        explorerGroups.stream().flatMap(g -> g.getExplorers().stream()).map(Explorer::getExplorerId).collect(Collectors.toList()))
                        )), Mono.fromRunnable(() -> keeperProfile.setStudyRequests(
                                courseRegistrationRequestService
                                        .getStudyRequestsForKeeper(authorizationHeader, keepers))
                        ), Mono.fromRunnable(() -> keeperProfile.setApprovedRequests(
                                courseRegistrationRequestService
                                        .getApprovedRequestsForKeeper(authorizationHeader, keepers))
                        ), Mono.fromRunnable(() -> keeperProfile.setFinalAssessments(
                                courseProgressService
                                        .getExplorersNeededFinalAssessment(authorizationHeader, explorerGroups))
                        ), Mono.fromRunnable(() -> keeperProfile.setReviewRequests(
                                homeworkService.getHomeworkRequestsFromExplorersByGroups(
                                        authorizationHeader,
                                        explorerGroups.stream().collect(Collectors.toMap(ExplorerGroup::getGroupId, g -> g))
                                )))
                ).parallel()
                .runOn(Schedulers.parallel())
                .then()
                .block();

        return keeperProfile;
    }
}
