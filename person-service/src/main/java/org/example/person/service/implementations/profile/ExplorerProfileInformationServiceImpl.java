package org.example.person.service.implementations.profile;

import org.example.person.dto.profile.ExplorerProfileDto;
import org.example.person.model.Explorer;
import org.example.person.repository.ExplorerRepository;
import org.example.person.service.api.course.CourseService;
import org.example.person.service.api.courseregistration.CourseRegistrationRequestService;
import org.example.person.service.api.feedback.FeedbackService;
import org.example.person.service.api.feedback.RatingService;
import org.example.person.service.api.homework.HomeworkService;
import org.example.person.service.api.profile.ExplorerListService;
import org.example.person.service.api.profile.ExplorerProfileInformationService;
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
public class ExplorerProfileInformationServiceImpl extends PersonProfileInformationService implements ExplorerProfileInformationService {
    private final ExplorerRepository explorerRepository;

    private final ExplorerListService explorerListService;
    private final CourseRegistrationRequestService courseRegistrationRequestService;
    private final CourseProgressService courseProgressService;
    private final HomeworkService homeworkService;

    public ExplorerProfileInformationServiceImpl(PersonService personService, RatingService ratingService, ExplorerRepository explorerRepository, ExplorerListService explorerListService, FeedbackService feedbackService, CourseRegistrationRequestService courseRegistrationRequestService, CourseService courseService, CourseProgressService courseProgressService, HomeworkService homeworkService, ModelMapper mapper) {
        super(ratingService, courseService, feedbackService, personService, mapper);
        this.explorerRepository = explorerRepository;
        this.explorerListService = explorerListService;
        this.courseRegistrationRequestService = courseRegistrationRequestService;
        this.courseProgressService = courseProgressService;
        this.homeworkService = homeworkService;
    }

    @Override
    @Transactional(readOnly = true)
    public ExplorerProfileDto getExplorerProfileInformation(String authorizationHeader, Long authenticatedPersonId) {
        List<Explorer> personExplorers = explorerRepository.findExplorersByPersonId(authenticatedPersonId);
        ExplorerProfileDto explorerProfile = addPersonProfileInformation(
                new ExplorerProfileDto(), authenticatedPersonId, personExplorers.size()
        );

        Flux.concat(
                        Mono.fromRunnable(() ->
                                explorerProfile.setRating(ratingService.getPersonRatingAsExplorer(authenticatedPersonId))
                        ), Mono.fromRunnable(() -> courseProgressService
                                .getCurrentCourseProgressProfile(authorizationHeader, authenticatedPersonId)
                                .ifPresent(explorerProfile::setCurrentSystem)
                        ),
                        Mono.fromRunnable(() -> explorerProfile.setExplorerFeedbacks(
                                feedbackService.getExplorerFeedbackOffers(
                                        authorizationHeader,
                                        personExplorers.stream().map(Explorer::getExplorerId).collect(Collectors.toList())
                                ))
                        ),
                        Mono.fromRunnable(() -> explorerProfile.setCourseFeedbacks(
                                feedbackService.getCourseRatingOffers(
                                        authorizationHeader,
                                        personExplorers.stream().map(Explorer::getExplorerId).collect(Collectors.toList())
                                ))
                        ),
                        Mono.fromRunnable(() -> courseRegistrationRequestService
                                .getStudyRequestForExplorerByPersonId(authorizationHeader)
                                .ifPresent(explorerProfile::setStudyRequest)),
                        Mono.fromRunnable(() -> explorerProfile.setInvestigatedSystems(
                                courseService.getCoursesRating(
                                        authorizationHeader,
                                        courseProgressService.getInvestigatedSystemIds(authorizationHeader, personExplorers)
                                ))
                        ),
                        Mono.fromRunnable(() -> explorerProfile.setRatingTable(
                                explorerListService
                                        .getExplorers(authorizationHeader, 0, 10)
                                        .getContent())
                        ),
                        Mono.fromRunnable(() -> explorerProfile.setHomeworkRequests(
                                homeworkService.getHomeworkRequestsFromPerson(
                                        authorizationHeader, personExplorers
                                ))
                        )
                ).parallel()
                .runOn(Schedulers.parallel())
                .then()
                .block();

        return explorerProfile;
    }
}
