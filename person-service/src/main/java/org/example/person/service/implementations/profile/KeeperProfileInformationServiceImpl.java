package org.example.person.service.implementations.profile;

import lombok.RequiredArgsConstructor;
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
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Mono;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class KeeperProfileInformationServiceImpl implements KeeperProfileInformationService {
    private final ExplorerGroupRepository explorerGroupRepository;
    private final KeeperRepository keeperRepository;

    private final PersonService personService;
    private final FeedbackService feedbackService;
    private final HomeworkService homeworkService;
    private final CourseRegistrationRequestService courseRegistrationRequestService;
    private final CourseProgressService courseProgressService;
    private final RatingService ratingService;
    private final CourseService courseService;

    @Override
    @Transactional(readOnly = true)
    public Map<String, Object> getKeeperProfileInformation(String authorizationHeader, Long authenticatedPersonId) {
        Map<String, Object> response = new LinkedHashMap<>();
        response.put("person", personService.findPersonById(authenticatedPersonId));
        response.put("rating", ratingService.getPersonRatingAsKeeper(authenticatedPersonId));
        List<Keeper> keepers = keeperRepository.findKeepersByPersonId(authenticatedPersonId);
        response.put("totalSystems", keepers.size());
        List<ExplorerGroup> explorerGroups = explorerGroupRepository.findExplorerGroupsByKeeperIdIn(
                keepers.stream().map(Keeper::getKeeperId).collect(Collectors.toList())
        );

        response.put("totalExplorers", explorerGroups.stream().mapToLong(g -> g.getExplorers().size()).sum());

        return Mono.when(
                Mono.fromRunnable(() -> response.put("systems", courseService.getCoursesRating(
                        authorizationHeader,
                        keepers.stream().map(Keeper::getCourseId).collect(Collectors.toList())
                ))), Mono.fromRunnable(() -> courseProgressService
                        .getCurrentGroup(authorizationHeader, explorerGroups)
                        .ifPresent(g ->
                                response.put("currentGroup", g)
                        )
                ), Mono.fromRunnable(() -> response.put(
                        "keeperFeedbacks",
                        feedbackService.getKeeperFeedbackOffers(
                                authorizationHeader,
                                explorerGroups.stream().flatMap(g -> g.getExplorers().stream()).map(Explorer::getExplorerId).collect(Collectors.toList()))
                )), Mono.fromRunnable(() -> response.put("studyRequests", courseRegistrationRequestService
                        .getStudyRequestsForKeeper(authorizationHeader, keepers))
                ), Mono.fromRunnable(() -> response.put("approvedRequests", courseRegistrationRequestService
                        .getApprovedRequestsForKeeper(authorizationHeader, keepers))
                ), Mono.fromRunnable(() -> response.put("finalAssessments", courseProgressService
                        .getExplorersNeededFinalAssessment(authorizationHeader, explorerGroups))
                ), Mono.fromRunnable(() -> response.put("reviewRequests", homeworkService.getHomeworkRequestsFromExplorersByGroups(
                        authorizationHeader,
                        explorerGroups.stream().collect(Collectors.toMap(ExplorerGroup::getGroupId, g -> g))
                )))
        ).then(Mono.just(response)).block();
    }
}
