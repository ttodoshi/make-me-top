package org.example.service;

import lombok.RequiredArgsConstructor;
import org.example.dto.explorer.ExplorerDto;
import org.example.dto.keeper.KeeperDto;
import org.example.repository.ExplorerRepository;
import org.example.repository.KeeperRepository;
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

    public Double getPersonRatingAsExplorer(Integer personId) {
        List<Integer> explorerIds = explorerRepository
                .findExplorersByPersonId(personId)
                .stream()
                .map(ExplorerDto::getExplorerId)
                .collect(Collectors.toList());
        return keeperFeedbackService.getRatingByPersonExplorerIds(explorerIds);
    }

    public Double getPersonRatingAsKeeper(Integer personId) {
        List<Integer> keeperIds = keeperRepository
                .findKeepersByPersonId(personId)
                .stream()
                .map(KeeperDto::getKeeperId)
                .collect(Collectors.toList());
        return explorerFeedbackService.getRatingByPersonKeeperIds(keeperIds);
    }

    @Transactional(readOnly = true)
    public Map<Integer, Double> getPeopleRatingAsExplorer(List<Integer> personIds) {
        return explorerRepository.findExplorersByPersonIdIn(personIds)
                .entrySet()
                .stream()
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        e -> keeperFeedbackService.getRatingByPersonExplorerIds(
                                e.getValue().stream().map(ExplorerDto::getExplorerId).collect(Collectors.toList())
                        )
                ));
    }

    @Transactional(readOnly = true)
    public Map<Integer, Double> getPeopleRatingAsKeeper(List<Integer> personIds) {
        return keeperRepository.findKeepersByPersonIdIn(personIds)
                .entrySet()
                .stream()
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        e -> explorerFeedbackService.getRatingByPersonKeeperIds(
                                e.getValue().stream().map(KeeperDto::getKeeperId).collect(Collectors.toList())
                        )
                ));
    }
}
