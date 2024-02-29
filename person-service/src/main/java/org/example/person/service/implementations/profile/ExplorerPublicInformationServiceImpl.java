package org.example.person.service.implementations.profile;

import org.example.person.config.security.RoleService;
import org.example.person.dto.feedback.KeeperCommentDto;
import org.example.person.dto.profile.ExplorerPublicProfileDto;
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
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ExplorerPublicInformationServiceImpl extends PersonProfileInformationService implements ExplorerPublicInformationService {
    private final ExplorerRepository explorerRepository;

    private final HomeworkService homeworkService;
    private final CourseRegistrationRequestService courseRegistrationRequestService;
    private final CourseProgressService courseProgressService;
    private final RoleService roleService;

    public ExplorerPublicInformationServiceImpl(PersonService personService, RatingService ratingService, ExplorerRepository explorerRepository, HomeworkService homeworkService, CourseService courseService, CourseRegistrationRequestService courseRegistrationRequestService, FeedbackService feedbackService, CourseProgressService courseProgressService, RoleService roleService, ModelMapper mapper) {
        super(ratingService, courseService, feedbackService, personService, mapper);
        this.explorerRepository = explorerRepository;
        this.homeworkService = homeworkService;
        this.courseRegistrationRequestService = courseRegistrationRequestService;
        this.courseProgressService = courseProgressService;
        this.roleService = roleService;
    }

    @Override
    @Transactional(readOnly = true)
    public ExplorerPublicProfileDto getExplorerPublicInformation(String authorizationHeader, Authentication authentication, Long personId) {
        List<Explorer> personExplorers = explorerRepository.findExplorersByPersonId(personId);
        ExplorerPublicProfileDto explorerPublicProfile = addPersonProfileInformation(
                new ExplorerPublicProfileDto(), personId, personExplorers.size()
        );

        Flux.concat(
                        Mono.fromRunnable(() ->
                                explorerPublicProfile.setRating(ratingService.getPersonRatingAsExplorer(personId))
                        ), Mono.fromRunnable(() -> {
                            Optional<CurrentCourseProgressPublicDto> currentCourseOptional = courseProgressService
                                    .getCurrentCourseProgressPublic(authorizationHeader, personId);

                            if (roleService.hasAnyAuthenticationRole(authentication.getAuthorities(), AuthenticationRoleType.KEEPER)) {
                                setInfoForKeeper(
                                        authorizationHeader, (Long) authentication.getPrincipal(),
                                        explorerPublicProfile, currentCourseOptional, personId, personExplorers
                                );
                            }
                            currentCourseOptional.ifPresent(explorerPublicProfile::setCurrentSystem);
                        }), Mono.fromRunnable(() -> explorerPublicProfile.setInvestigatedSystems(
                                courseService.getCoursesRating(
                                        authorizationHeader,
                                        courseProgressService.getInvestigatedSystemIds(authorizationHeader, personExplorers)
                                ))
                        ), Mono.fromRunnable(() -> {
                            List<KeeperCommentDto> feedbackList = feedbackService.getFeedbackForPersonAsExplorer(
                                    authorizationHeader, personExplorers
                            );
                            explorerPublicProfile.setTotalFeedback(feedbackList.size());
                            explorerPublicProfile.setFeedback(feedbackList);
                        })
                ).parallel()
                .runOn(Schedulers.parallel())
                .then()
                .block();

        return explorerPublicProfile;
    }

    private void setInfoForKeeper(String authorizationHeader, Long authenticatedPersonId, ExplorerPublicProfileDto explorerPublicProfile, Optional<CurrentCourseProgressPublicDto> currentCourseOptional, Long personId, List<Explorer> personExplorers) {
        currentCourseOptional.ifPresentOrElse(c -> {
            if (c.getKeeper().getPersonId().equals(authenticatedPersonId)) {
                explorerPublicProfile.setReviewRequests(
                        homeworkService.getHomeworkRequestsFromPerson(
                                authorizationHeader,
                                personExplorers.stream()
                                        .filter(e -> e.getExplorerId().equals(c.getExplorerId()))
                                        .collect(Collectors.toList())
                        )
                );
            }
        }, () -> courseRegistrationRequestService
                .getStudyRequesForKeepertByExplorerPersonId(authorizationHeader, authenticatedPersonId, personId)
                .ifPresent(explorerPublicProfile::setStudyRequest));
    }
}
