package org.example.feedback.service;

import lombok.RequiredArgsConstructor;
import org.example.feedback.dto.feedback.CreateCourseRatingDto;
import org.example.feedback.dto.feedback.CreateExplorerFeedbackDto;
import org.example.feedback.exception.classes.explorer.ExplorerNotFoundException;
import org.example.feedback.repository.CourseRatingRepository;
import org.example.feedback.repository.ExplorerFeedbackRepository;
import org.example.feedback.repository.ExplorerRepository;
import org.example.feedback.service.validator.FeedbackValidatorService;
import org.example.grpc.ExplorersService;
import org.example.feedback.model.CourseRating;
import org.example.feedback.model.ExplorerFeedback;
import org.modelmapper.ModelMapper;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;

@Service
@RequiredArgsConstructor
public class ExplorerFeedbackService {
    private final ExplorerRepository explorerRepository;
    private final ExplorerFeedbackRepository explorerFeedbackRepository;
    private final CourseRatingRepository courseRatingRepository;

    private final PersonService personService;
    private final FeedbackValidatorService feedbackValidatorService;

    private final CacheManager cacheManager;
    private final ModelMapper mapper;

    @Transactional(readOnly = true)
    public List<ExplorerFeedback> findExplorerFeedbacksByKeeperIdIn(List<Long> keeperIds) {
        return explorerFeedbackRepository.findExplorerFeedbacksByKeeperIdIn(keeperIds);
    }

    @Transactional
    public ExplorerFeedback sendFeedbackForKeeper(Long courseId, CreateExplorerFeedbackDto feedback) {
        Long personId = personService.getAuthenticatedPersonId();
        feedbackValidatorService.validateFeedbackForKeeperRequest(personId, feedback);
        ExplorersService.Explorer explorer = explorerRepository
                .findExplorerByPersonIdAndGroup_CourseId(personId, courseId)
                .orElseThrow(() -> new ExplorerNotFoundException(courseId));
        ExplorerFeedback savingFeedback = mapper.map(feedback, ExplorerFeedback.class);
        savingFeedback.setExplorerId(explorer.getExplorerId());
        clearKeeperRatingCache(feedback.getKeeperId());
        return explorerFeedbackRepository.save(savingFeedback);
    }

    @Async
    public void clearKeeperRatingCache(Long keeperId) {
        CompletableFuture.runAsync(() -> {
            Cache keeperRatingCache = cacheManager.getCache("keeperRatingCache");
            Map<List<Long>, Double> nativeCache = (Map<List<Long>, Double>)
                    Objects.requireNonNull(keeperRatingCache).getNativeCache();
            for (Map.Entry<List<Long>, Double> entry : nativeCache.entrySet()) {
                if (entry.getKey().contains(keeperId))
                    keeperRatingCache.evict(entry.getKey());
            }
        });
    }

    @Transactional
    public CourseRating rateCourse(Long courseId, CreateCourseRatingDto request) {
        Long personId = personService.getAuthenticatedPersonId();
        feedbackValidatorService.validateCourseRatingRequest(personId, courseId, request);
        ExplorersService.Explorer explorer = explorerRepository
                .findExplorerByPersonIdAndGroup_CourseId(personId, courseId)
                .orElseThrow(() -> new ExplorerNotFoundException(courseId));
        return courseRatingRepository.save(
                new CourseRating(explorer.getExplorerId(), request.getRating())
        );
    }

    @Cacheable(cacheNames = "keeperRatingCache", key = "#keeperIds")
    @Transactional(readOnly = true)
    public Double getRatingByPersonKeeperIds(List<Long> keeperIds) {
        return Math.ceil(explorerFeedbackRepository.getPersonRatingAsKeeper(keeperIds).orElse(0.0) * 10) / 10;
    }
}