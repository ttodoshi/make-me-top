package org.example.feedback.service;

import lombok.RequiredArgsConstructor;
import org.example.feedback.repository.CourseRatingRepository;
import org.example.feedback.repository.ExplorerRepository;
import org.example.grpc.ExplorersService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CourseRatingService {
    private final CourseRatingRepository courseRatingRepository;
    private final ExplorerRepository explorerRepository;

    @Transactional(readOnly = true)
    public Map<Integer, Double> getCoursesRating(List<Integer> courseIds) {
        Map<Integer, ExplorersService.ExplorerList> explorers = explorerRepository.findExplorersByGroup_CourseIdIn(courseIds);
        return explorers.entrySet()
                .stream()
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        e -> courseRatingRepository.findAvgRatingByExplorerIdIn(
                                e.getValue()
                                        .getExplorersList()
                                        .stream()
                                        .map(ExplorersService.Explorer::getExplorerId)
                                        .collect(Collectors.toList())
                        ).orElse(0.0)
                ));
    }
}
