package org.example.service;

import lombok.RequiredArgsConstructor;
import org.example.config.security.RoleService;
import org.example.config.security.role.AuthenticationRoleType;
import org.example.dto.feedback.KeeperCommentDto;
import org.example.dto.progress.CurrentCourseProgressDto;
import org.example.model.Explorer;
import org.example.model.Person;
import org.example.repository.ExplorerRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.Executor;

@Service
@RequiredArgsConstructor
public class ExplorerPublicInformationService {
    private final ExplorerRepository explorerRepository;

    private final PersonService personService;
    private final HomeworkService homeworkService;
    private final CourseService courseService;
    private final CourseRegistrationRequestService courseRegistrationRequestService;
    private final FeedbackService feedbackService;
    private final RatingService ratingService;
    private final CourseProgressService courseProgressService;
    private final RoleService roleService;

    private final Executor asyncExecutor;

    @Transactional(readOnly = true)
    public Map<String, Object> getExplorerPublicInformation(Integer personId) {
        Map<String, Object> response = new LinkedHashMap<>();
        Person person = personService.findPersonById(personId);
        Person authenticatedPerson = personService.getAuthenticatedPerson();
        Integer authenticatedPersonId = authenticatedPerson.getPersonId();
        response.put("person", person);
        response.put("rating", ratingService.getPersonRatingAsExplorer(personId));
        List<Explorer> personExplorers = explorerRepository.findExplorersByPersonId(personId);
        response.put("totalSystems", personExplorers.size());
        CompletableFuture<Void> currentSystem = CompletableFuture.runAsync(() -> {
            Optional<CurrentCourseProgressDto> currentCourseOptional = courseProgressService
                    .getCurrentCourseProgress(personId);
            if (currentCourseOptional.isEmpty()) {
                courseRegistrationRequestService
                        .getStudyRequestByExplorerPersonId(authenticatedPersonId, personId)
                        .ifPresent(
                                r -> response.put("studyRequest", r));
            } else {
                if (roleService.hasAnyAuthenticationRole(AuthenticationRoleType.KEEPER)) {
                    homeworkService.getHomeworkRequestForKeeperFromPerson(authenticatedPersonId, personExplorers).ifPresent(
                            hr -> response.put("reviewRequest", hr)
                    );
                }
                response.put("currentSystem", currentCourseOptional.get());
            }
        }, asyncExecutor);
        CompletableFuture<Void> investigatedSystems = CompletableFuture.runAsync(() ->
                response.put("investigatedSystems", courseService.getCoursesRating(
                        courseProgressService.getInvestigatedSystemIds(personExplorers)
                )), asyncExecutor);
        CompletableFuture<Void> feedback = CompletableFuture.runAsync(() -> {
            List<KeeperCommentDto> feedbackList = feedbackService.getFeedbackForPersonAsExplorer(personExplorers);
            response.put("totalFeedback", feedbackList.size());
            response.put("feedback", feedbackList);
        }, asyncExecutor);
        try {
            CompletableFuture.allOf(currentSystem, investigatedSystems, feedback).join();
        } catch (CompletionException completionException) {
            try {
                throw completionException.getCause();
            } catch (Throwable e) {
                throw new RuntimeException(e);
            }
        }
        return response;
    }
}
