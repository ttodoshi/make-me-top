package org.example.service;

import lombok.RequiredArgsConstructor;
import org.example.model.Explorer;
import org.example.repository.ExplorerRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.Executor;

@Service
@RequiredArgsConstructor
public class ExplorerProfileInformationService {
    private final ExplorerRepository explorerRepository;

    private final PersonService personService;
    private final ExplorerListService explorerListService;
    private final CourseRegistrationRequestService courseRegistrationRequestService;
    private final CourseService courseService;
    private final RatingService ratingService;
    private final CourseProgressService courseProgressService;

    private final Executor asyncExecutor;

    @Transactional(readOnly = true)
    public Map<String, Object> getExplorerCabinetInformation() {
        Map<String, Object> response = new LinkedHashMap<>();
        Integer authenticatedPersonId = personService.getAuthenticatedPersonId();
        response.put("person", personService.getAuthenticatedPerson());
        response.put("rating", ratingService.getPersonRatingAsExplorer(authenticatedPersonId));
        List<Explorer> personExplorers = explorerRepository.findExplorersByPersonId(authenticatedPersonId);
        response.put("totalSystems", personExplorers.size());
        CompletableFuture<Void> currentSystem = CompletableFuture.runAsync(() ->
                courseProgressService.getCurrentCourseProgress(authenticatedPersonId)
                        .ifPresent(p -> response.put("currentSystem", p)), asyncExecutor);
        CompletableFuture<Void> studyRequest = CompletableFuture.runAsync(() ->
                courseRegistrationRequestService.getStudyRequestForExplorerByPersonId()
                        .ifPresent(r -> response.put("studyRequest", r)), asyncExecutor);
        CompletableFuture<Void> investigatedSystems = CompletableFuture.runAsync(() ->
                response.put("investigatedSystems", courseService.getCoursesRating(
                        courseProgressService.getInvestigatedSystemIds(personExplorers)
                )), asyncExecutor);
        CompletableFuture<Void> ratingTable = CompletableFuture.runAsync(() ->
                        response.put("ratingTable", explorerListService.getExplorers()),
                asyncExecutor);
        try {
            CompletableFuture.allOf(currentSystem, studyRequest, investigatedSystems, ratingTable).join();
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
