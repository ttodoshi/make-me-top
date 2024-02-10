package org.example.feedback.service.implementations;

import lombok.RequiredArgsConstructor;
import org.example.feedback.service.*;
import org.example.grpc.ExplorersService;
import org.example.grpc.KeepersService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RatingServiceImpl implements RatingService {
    private final ExplorerService explorerService;
    private final KeeperService keeperService;
    private final KeeperFeedbackService keeperFeedbackService;
    private final ExplorerFeedbackService explorerFeedbackService;

    @Override
    public Double getPersonRatingAsExplorer(Long personId) {
        List<Long> explorerIds = explorerService
                .findExplorersByPersonId(personId)
                .stream()
                .map(ExplorersService.Explorer::getExplorerId)
                .collect(Collectors.toList());
        return keeperFeedbackService.getRatingByPersonExplorerIds(explorerIds);
    }

    @Override
    public Double getPersonRatingAsKeeper(Long personId) {
        List<Long> keeperIds = keeperService
                .findKeepersByPersonId(personId)
                .stream()
                .map(KeepersService.Keeper::getKeeperId)
                .collect(Collectors.toList());
        return explorerFeedbackService.getRatingByPersonKeeperIds(keeperIds);
    }

    @Override
    @Transactional(readOnly = true)
    public Map<Long, Double> getPeopleRatingAsExplorer(List<Long> personIds) {
        return explorerService.findExplorersByPersonIdIn(personIds)
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

    @Override
    @Transactional(readOnly = true)
    public Map<Long, Double> getPeopleRatingAsKeeper(List<Long> personIds) {
        return keeperService.findKeepersByPersonIdIn(personIds)
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
