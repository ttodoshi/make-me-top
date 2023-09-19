package org.example.service;

import lombok.RequiredArgsConstructor;
import org.example.dto.explorer.ExplorerDto;
import org.example.dto.keeper.KeeperDto;
import org.example.repository.ExplorerFeedbackRepository;
import org.example.repository.ExplorerRepository;
import org.example.repository.KeeperFeedbackRepository;
import org.example.repository.KeeperRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RatingService {
    private final ExplorerRepository explorerRepository;
    private final KeeperRepository keeperRepository;
    private final KeeperFeedbackRepository keeperFeedbackRepository;
    private final ExplorerFeedbackRepository explorerFeedbackRepository;

    public Double getPersonRatingAsExplorer(Integer personId) {
        List<Integer> explorerIds = explorerRepository
                .findExplorersByPersonId(personId)
                .stream()
                .mapToInt(ExplorerDto::getExplorerId)
                .boxed()
                .collect(Collectors.toList());
        return Math.ceil(keeperFeedbackRepository.getPersonRatingAsExplorer(explorerIds).orElse(0.0) * 10) / 10;
    }

    public Double getPersonRatingAsKeeper(Integer personId) {
        List<Integer> keeperIds = keeperRepository
                .findKeepersByPersonId(personId)
                .stream()
                .mapToInt(KeeperDto::getKeeperId)
                .boxed()
                .collect(Collectors.toList());
        return Math.ceil(explorerFeedbackRepository.getPersonRatingAsKeeper(keeperIds).orElse(0.0) * 10) / 10;
    }

    public Map<Integer, Double> getPeopleRatingAsExplorer(List<Integer> personIds) {
        return explorerRepository.findExplorersByPersonIdIn(personIds)
                .entrySet()
                .stream()
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        e -> Math.ceil(keeperFeedbackRepository.getPersonRatingAsExplorer(
                                e.getValue().stream().map(ExplorerDto::getExplorerId).collect(Collectors.toList())
                        ).orElse(0.0) * 10) / 10
                ));
    }

    public Map<Integer, Double> getPeopleRatingAsKeeper(List<Integer> personIds) {
        return keeperRepository.findKeepersByPersonIdIn(personIds)
                .entrySet()
                .stream()
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        e -> Math.ceil(explorerFeedbackRepository.getPersonRatingAsKeeper(
                                e.getValue().stream().map(KeeperDto::getKeeperId).collect(Collectors.toList())
                        ).orElse(0.0) * 10) / 10
                ));
    }
}
