package org.example.feedback.service.implementations;

import lombok.RequiredArgsConstructor;
import org.example.feedback.repository.CourseRatingRepository;
import org.example.feedback.service.CourseRatingService;
import org.example.feedback.service.ExplorerService;
import org.example.grpc.ExplorersService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CourseRatingServiceImpl implements CourseRatingService {
    private final CourseRatingRepository courseRatingRepository;
    private final ExplorerService explorerService;

    @Override
    @Transactional(readOnly = true)
    public Map<Long, Double> getCoursesRating(String authorizationHeader, List<Long> courseIds) {
        Map<Long, ExplorersService.ExplorerList> explorers = explorerService
                .findExplorersByGroup_CourseIdIn(authorizationHeader, courseIds);
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
