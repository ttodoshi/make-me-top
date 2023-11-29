package org.example.person.service;

import lombok.RequiredArgsConstructor;
import org.example.person.model.ExplorerGroup;
import org.example.person.model.Keeper;
import org.example.person.repository.ExplorerGroupRepository;
import org.example.person.repository.KeeperRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.Executor;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class KeeperProfileInformationService {
    private final ExplorerGroupRepository explorerGroupRepository;
    private final KeeperRepository keeperRepository;

    private final PersonService personService;
    private final HomeworkService homeworkService;
    private final CourseRegistrationRequestService courseRegistrationRequestService;
    private final CourseProgressService courseProgressService;
    private final RatingService ratingService;

    private final Executor asyncExecutor;

    @Transactional(readOnly = true)
    public Map<String, Object> getKeeperCabinetInformation() {
        Map<String, Object> response = new LinkedHashMap<>();
        Long authenticatedPersonId = personService.getAuthenticatedPersonId();
        response.put("person", personService.getAuthenticatedPerson());
        response.put("rating", ratingService.getPersonRatingAsKeeper(authenticatedPersonId));
        List<Keeper> keepers = keeperRepository.findKeepersByPersonId(authenticatedPersonId);
        response.put("totalSystems", keepers.size());
        List<ExplorerGroup> explorerGroups = explorerGroupRepository.findExplorerGroupsByKeeperIdIn(
                keepers.stream().map(Keeper::getKeeperId).collect(Collectors.toList())
        );
        response.put("totalExplorers", explorerGroups.stream().mapToLong(g -> g.getExplorers().size()).sum());

        CompletableFuture<Void> currentGroup = CompletableFuture.runAsync(() ->
                courseProgressService.getCurrentGroup(explorerGroups).ifPresent(g ->
                        response.put("currentGroup", g)
                ), asyncExecutor);

        CompletableFuture<Void> studyRequests = CompletableFuture.runAsync(() ->
                response.put("studyRequests", courseRegistrationRequestService
                        .getStudyRequestsForKeeper(keepers)), asyncExecutor);

        CompletableFuture<Void> acceptedRequests = CompletableFuture.runAsync(() ->
                response.put("approvedRequests", courseRegistrationRequestService
                        .getApprovedRequestsForKeeper(keepers)), asyncExecutor);

        CompletableFuture<Void> finalAssessments = CompletableFuture.runAsync(() ->
                response.put("finalAssessments", courseProgressService
                        .getExplorersNeededFinalAssessment(explorerGroups)), asyncExecutor);

        CompletableFuture<Void> reviewRequests = CompletableFuture.runAsync(() ->
                response.put("reviewRequests", homeworkService.getHomeworkRequestsFromExplorersByGroups(
                        explorerGroups.stream().collect(Collectors.toMap(ExplorerGroup::getGroupId, g -> g))
                )), asyncExecutor);

        try {
            CompletableFuture.allOf(currentGroup, studyRequests, acceptedRequests, finalAssessments, reviewRequests).join();
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
