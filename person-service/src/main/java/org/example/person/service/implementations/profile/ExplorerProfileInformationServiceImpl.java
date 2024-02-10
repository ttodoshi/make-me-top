package org.example.person.service.implementations.profile;

import lombok.RequiredArgsConstructor;
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
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Mono;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ExplorerProfileInformationServiceImpl implements ExplorerProfileInformationService {
    private final ExplorerRepository explorerRepository;

    private final PersonService personService;
    private final ExplorerListService explorerListService;
    private final FeedbackService feedbackService;
    private final CourseRegistrationRequestService courseRegistrationRequestService;
    private final CourseService courseService;
    private final RatingService ratingService;
    private final CourseProgressService courseProgressService;
    private final HomeworkService homeworkService;

    @Override
    @Transactional(readOnly = true)
    public Map<String, Object> getExplorerProfileInformation(String authorizationHeader, Long authenticatedPersonId) {
        Map<String, Object> response = new LinkedHashMap<>();
        response.put("person", personService.findPersonById(authenticatedPersonId));
        response.put("rating", ratingService.getPersonRatingAsExplorer(authenticatedPersonId));
        List<Explorer> personExplorers = explorerRepository.findExplorersByPersonId(authenticatedPersonId);
        response.put("totalSystems", personExplorers.size());

        return Mono.when(
                Mono.fromRunnable(() ->
                        courseProgressService
                                .getCurrentCourseProgressProfile(authorizationHeader, authenticatedPersonId)
                                .ifPresent(p -> response.put("currentSystem", p))
                ),
                Mono.fromRunnable(() -> response.put(
                        "explorerFeedbacks",
                        feedbackService.getExplorerFeedbackOffers(
                                authorizationHeader,
                                personExplorers.stream().map(Explorer::getExplorerId).collect(Collectors.toList())
                        )
                )),
                Mono.fromRunnable(() -> response.put(
                        "courseFeedbacks",
                        feedbackService.getCourseRatingOffers(
                                authorizationHeader,
                                personExplorers.stream().map(Explorer::getExplorerId).collect(Collectors.toList())
                        )
                )),
                Mono.fromSupplier(() -> courseRegistrationRequestService
                        .getStudyRequestForExplorerByPersonId(authorizationHeader)
                        .map(r -> response.put("studyRequest", r))),
                Mono.fromRunnable(() -> response.put("investigatedSystems", courseService.getCoursesRating(
                        authorizationHeader,
                        courseProgressService.getInvestigatedSystemIds(authorizationHeader, personExplorers)
                ))),
                Mono.fromRunnable(() -> response.put(
                        "ratingTable",
                        explorerListService.getExplorers(authorizationHeader, 0, 10)
                                .getContent()
                )),
                Mono.fromRunnable(() -> response.put("homeworkRequests", homeworkService.getHomeworkRequestsFromPerson(
                        authorizationHeader, personExplorers
                )))
        ).then(Mono.just(response)).block();
    }
}
