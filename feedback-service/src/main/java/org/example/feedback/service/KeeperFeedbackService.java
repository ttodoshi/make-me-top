package org.example.feedback.service;

import lombok.RequiredArgsConstructor;
import org.example.feedback.dto.feedback.CreateKeeperFeedbackDto;
import org.example.feedback.exception.classes.course.CourseNotFoundException;
import org.example.feedback.exception.classes.keeper.KeeperNotFoundException;
import org.example.feedback.repository.CourseRepository;
import org.example.feedback.service.validator.FeedbackValidatorService;
import org.example.grpc.KeepersService;
import org.example.feedback.model.KeeperFeedback;
import org.example.feedback.repository.KeeperFeedbackRepository;
import org.example.feedback.repository.KeeperRepository;
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
public class KeeperFeedbackService {
    private final KeeperFeedbackRepository keeperFeedbackRepository;
    private final CourseRepository courseRepository;
    private final KeeperRepository keeperRepository;

    private final PersonService personService;
    private final FeedbackValidatorService feedbackValidatorService;

    private final CacheManager cacheManager;
    private final ModelMapper mapper;

    @Transactional(readOnly = true)
    public List<KeeperFeedback> findKeeperFeedbacksByExplorerIdIn(List<Long> explorerIds) {
        return keeperFeedbackRepository.findKeeperFeedbacksByExplorerIdIn(explorerIds);
    }

    @Transactional
    public KeeperFeedback sendFeedbackForExplorer(Long courseId, CreateKeeperFeedbackDto feedback) {
        if (!courseRepository.existsById(courseId))
            throw new CourseNotFoundException(courseId);
        Long personId = personService.getAuthenticatedPersonId();
        KeepersService.Keeper keeper = keeperRepository.findKeeperByPersonIdAndCourseId(personId, courseId)
                .orElseThrow(KeeperNotFoundException::new);
        feedbackValidatorService.validateFeedbackForExplorerRequest(keeper.getKeeperId(), feedback);
        KeeperFeedback savingFeedback = mapper.map(feedback, KeeperFeedback.class);
        savingFeedback.setKeeperId(keeper.getKeeperId());
        clearExplorerRatingCache(feedback.getExplorerId());
        return keeperFeedbackRepository.save(savingFeedback);
    }

    @Async
    public void clearExplorerRatingCache(Long explorerId) {
        CompletableFuture.runAsync(() -> {
            Cache explorerRatingCache = cacheManager.getCache("explorerRatingCache");
            Map<List<Long>, Double> nativeCache = (Map<List<Long>, Double>)
                    Objects.requireNonNull(explorerRatingCache).getNativeCache();
            for (Map.Entry<List<Long>, Double> entry : nativeCache.entrySet()) {
                if (entry.getKey().contains(explorerId))
                    explorerRatingCache.evict(entry.getKey());
            }
        });
    }

    @Cacheable(cacheNames = "explorerRatingCache", key = "#explorerIds")
    @Transactional(readOnly = true)
    public Double getRatingByPersonExplorerIds(List<Long> explorerIds) {
        return Math.ceil(keeperFeedbackRepository.getPersonRatingAsExplorer(explorerIds).orElse(0.0) * 10) / 10;
    }
}
