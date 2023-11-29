package org.example.feedback.service;

import lombok.RequiredArgsConstructor;
import org.example.feedback.repository.ExplorerRepository;
import org.example.grpc.ExplorersService;
import org.example.grpc.KeepersService;
import org.example.feedback.repository.KeeperRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RatingService {
    private final ExplorerRepository explorerRepository;
    private final KeeperRepository keeperRepository;
    private final KeeperFeedbackService keeperFeedbackService;
    private final ExplorerFeedbackService explorerFeedbackService;

    public Double getPersonRatingAsExplorer(Long personId) {
        List<Long> explorerIds = explorerRepository
                .findExplorersByPersonId(personId)
                .stream()
                .map(ExplorersService.Explorer::getExplorerId)
                .collect(Collectors.toList());
        return keeperFeedbackService.getRatingByPersonExplorerIds(explorerIds);
    }

    public Double getPersonRatingAsKeeper(Long personId) {
        List<Long> keeperIds = keeperRepository
                .findKeepersByPersonId(personId)
                .stream()
                .map(KeepersService.Keeper::getKeeperId)
                .collect(Collectors.toList());
        return explorerFeedbackService.getRatingByPersonKeeperIds(keeperIds);
    }

    @Transactional(readOnly = true)
    public Map<Long, Double> getPeopleRatingAsExplorer(List<Long> personIds) {
        return explorerRepository.findExplorersByPersonIdIn(personIds)
                .entrySet()
                .stream()
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        e -> keeperFeedbackService.getRatingByPersonExplorerIds(
                                e.getValue()
                                        .getExplorersList()
                                        .stream()
                                        .map(ExplorersService.Explorer::getExplorerId)
                                        .collect(Collectors.toList())
                        )
                ));
    }

    @Transactional(readOnly = true)
    public Map<Long, Double> getPeopleRatingAsKeeper(List<Long> personIds) {
        return keeperRepository.findKeepersByPersonIdIn(personIds)
                .entrySet()
                .stream()
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        e -> explorerFeedbackService.getRatingByPersonKeeperIds(
                                e.getValue()
                                        .getKeepersList()
                                        .stream()
                                        .map(KeepersService.Keeper::getKeeperId)
                                        .collect(Collectors.toList())
                        )
                ));
    }
}
