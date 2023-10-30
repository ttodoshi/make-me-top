package org.example.service;

import lombok.RequiredArgsConstructor;
import org.example.model.Explorer;
import org.example.model.ExplorerGroup;
import org.example.model.Keeper;
import org.example.model.Person;
import org.example.repository.ExplorerGroupRepository;
import org.example.repository.KeeperRepository;
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
public class KeeperPublicInformationService {
    private final ExplorerGroupRepository explorerGroupRepository;
    private final KeeperRepository keeperRepository;

    private final PersonService personService;
    private final FeedbackService feedbackService;
    private final CourseService courseService;
    private final RatingService ratingService;

    private final Executor asyncExecutor;

    @Transactional(readOnly = true)
    public Map<String, Object> getKeeperPublicInformation(Integer personId) {
        Map<String, Object> response = new LinkedHashMap<>();
        Person person = personService.findPersonById(personId);
        response.put("person", person);
        response.put("rating", ratingService.getPersonRatingAsKeeper(personId));
        List<Keeper> keepers = keeperRepository.findKeepersByPersonId(personId);
        response.put("totalSystems", keepers.size());
        List<ExplorerGroup> explorerGroups = explorerGroupRepository.findExplorerGroupsByKeeperIdIn(
                keepers.stream().map(Keeper::getKeeperId).collect(Collectors.toList())
        );
        List<Explorer> explorers = explorerGroups
                .stream()
                .flatMap(g -> g.getExplorers().stream())
                .collect(Collectors.toList());
        response.put("totalExplorers", explorers.size());
        CompletableFuture<Void> systems = CompletableFuture.runAsync(() ->
                response.put("systems", courseService.getCoursesRating(
                        keepers.stream().map(Keeper::getCourseId).collect(Collectors.toList())
                )), asyncExecutor);
        CompletableFuture<Void> feedback = CompletableFuture.runAsync(() ->
                response.put("feedback", feedbackService.getFeedbackForPersonAsKeeper(explorerGroups)), asyncExecutor);
        try {
            CompletableFuture.allOf(systems, feedback).join();
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
