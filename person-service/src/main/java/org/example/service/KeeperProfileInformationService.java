package org.example.service;

import lombok.RequiredArgsConstructor;
import org.example.model.ExplorerGroup;
import org.example.model.Keeper;
import org.example.model.Person;
import org.example.repository.ExplorerGroupRepository;
import org.example.repository.KeeperRepository;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class KeeperProfileInformationService {
    private final ExplorerGroupRepository explorerGroupRepository;
    private final KeeperRepository keeperRepository;

    private final HomeworkService homeworkService;
    private final CourseRegistrationRequestService courseRegistrationRequestService;
    private final CourseProgressService courseProgressService;
    private final RatingService ratingService;

    private final Executor asyncExecutor;

    @Transactional(readOnly = true)
    public Map<String, Object> getKeeperCabinetInformation() {
        Person authenticatedPerson = (Person) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Integer authenticatedPersonId = authenticatedPerson.getPersonId();
        Map<String, Object> response = new LinkedHashMap<>();
        response.put("person", authenticatedPerson);
        response.put("rating", ratingService.getPersonRatingAsKeeper(authenticatedPersonId));
        List<Keeper> keepers = keeperRepository.findKeepersByPersonId(authenticatedPersonId);
        response.put("totalSystems", keepers.size());
        List<ExplorerGroup> explorerGroups = explorerGroupRepository.findExplorerGroupsByKeeperIdIn(
                keepers.stream().map(Keeper::getKeeperId).collect(Collectors.toList())
        );
        response.put("totalExplorers", explorerGroups.stream().mapToLong(g -> g.getExplorers().size()).sum());
        CompletableFuture<Void> studyingExplorers = CompletableFuture.runAsync(() -> {
            response.put("studyingExplorers", courseProgressService.getStudyingExplorersByKeeperPersonId(explorerGroups));
        }, asyncExecutor);
        CompletableFuture<Void> studyRequests = CompletableFuture.runAsync(() -> {
            response.put("studyRequests", courseRegistrationRequestService.getStudyRequestsForKeeper(keepers));
        }, asyncExecutor);
        CompletableFuture<Void> finalAssessments = CompletableFuture.runAsync(() -> {
            response.put("finalAssessments", courseProgressService.getExplorersNeededFinalAssessment(explorerGroups));
        }, asyncExecutor);
        CompletableFuture<Void> reviewRequests = CompletableFuture.runAsync(() -> {
            response.put("reviewRequests", homeworkService.getHomeworkRequestsFromExplorersByGroups(
                    explorerGroups.stream().collect(Collectors.toMap(ExplorerGroup::getGroupId, g -> g))
            ));
        }, asyncExecutor);
        CompletableFuture.allOf(studyingExplorers, studyRequests, finalAssessments, reviewRequests).join();
        return response;
    }
}
