package org.example.person.service.implementations.profile;

import lombok.RequiredArgsConstructor;
import org.example.person.config.security.RoleService;
import org.example.person.dto.feedback.KeeperCommentDto;
import org.example.person.dto.person.GetPersonDto;
import org.example.person.dto.progress.CurrentCourseProgressPublicDto;
import org.example.person.enums.AuthenticationRoleType;
import org.example.person.model.Explorer;
import org.example.person.repository.ExplorerRepository;
import org.example.person.service.api.course.CourseService;
import org.example.person.service.api.courseregistration.CourseRegistrationRequestService;
import org.example.person.service.api.feedback.FeedbackService;
import org.example.person.service.api.feedback.RatingService;
import org.example.person.service.api.homework.HomeworkService;
import org.example.person.service.api.profile.ExplorerPublicInformationService;
import org.example.person.service.api.progress.CourseProgressService;
import org.example.person.service.implementations.PersonService;
import org.modelmapper.ModelMapper;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Mono;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ExplorerPublicInformationServiceImpl implements ExplorerPublicInformationService {
    private final ExplorerRepository explorerRepository;

    private final PersonService personService;
    private final HomeworkService homeworkService;
    private final CourseService courseService;
    private final CourseRegistrationRequestService courseRegistrationRequestService;
    private final FeedbackService feedbackService;
    private final RatingService ratingService;
    private final CourseProgressService courseProgressService;
    private final RoleService roleService;

    private final ModelMapper mapper;

    @Override
    @Transactional(readOnly = true)
    public Map<String, Object> getExplorerPublicInformation(String authorizationHeader, Authentication authentication, Long personId) {
        Map<String, Object> response = new LinkedHashMap<>();
        response.put(
                "person",
                mapper.map(
                        personService.findPersonById(personId),
                        GetPersonDto.class
                )
        );
        response.put("rating", ratingService.getPersonRatingAsExplorer(personId));
        List<Explorer> personExplorers = explorerRepository.findExplorersByPersonId(personId);
        response.put("totalSystems", personExplorers.size());

        return Mono.when(
                Mono.fromRunnable(() -> {
                    Optional<CurrentCourseProgressPublicDto> currentCourseOptional = courseProgressService
                            .getCurrentCourseProgressPublic(authorizationHeader, personId);

                    if (currentCourseOptional.isEmpty()) {
                        if (roleService.hasAnyAuthenticationRole(authentication.getAuthorities(), AuthenticationRoleType.KEEPER)) {
                            courseRegistrationRequestService
                                    .getStudyRequestByExplorerPersonId(authorizationHeader, (Long) authentication.getPrincipal(), personId)
                                    .ifPresent(
                                            r -> response.put("studyRequest", r));
                        }
                    } else {
                        CurrentCourseProgressPublicDto currentCourse = currentCourseOptional.get();

                        if (roleService.hasAnyAuthenticationRole(authentication.getAuthorities(), AuthenticationRoleType.KEEPER) &&
                                currentCourse.getKeeper().getPersonId().equals((Long) authentication.getPrincipal())) {
                            response.put(
                                    "reviewRequests",
                                    homeworkService.getHomeworkRequestsFromPerson(
                                            authorizationHeader,
                                            personExplorers.stream()
                                                    .filter(e -> e.getExplorerId().equals(currentCourse.getExplorerId()))
                                                    .collect(Collectors.toList())
                                    )
                            );
                        }
                        response.put("currentSystem", currentCourse);
                    }
                }), Mono.fromRunnable(() -> response.put("investigatedSystems", courseService.getCoursesRating(
                        authorizationHeader,
                        courseProgressService.getInvestigatedSystemIds(authorizationHeader, personExplorers)
                ))), Mono.fromRunnable(() -> {
                    List<KeeperCommentDto> feedbackList = feedbackService.getFeedbackForPersonAsExplorer(
                            authorizationHeader, personExplorers
                    );
                    response.put("totalFeedback", feedbackList.size());
                    response.put("feedback", feedbackList);
                })
        ).then(Mono.just(response)).block();
    }
}
