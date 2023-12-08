package org.example.feedback.aspect;

import lombok.RequiredArgsConstructor;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.example.feedback.dto.feedback.CreateExplorerFeedbackDto;
import org.example.feedback.dto.feedback.CreateKeeperFeedbackDto;
import org.example.feedback.dto.feedback.ExplorerFeedbackDto;
import org.example.feedback.dto.feedback.KeeperFeedbackDto;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;

@Aspect
@Component
@RequiredArgsConstructor
public class CacheEvictionAspect {
    private final CacheManager cacheManager;

    @Pointcut(value = "execution(* org.example.feedback.service.ExplorerFeedbackService.sendFeedbackForKeeper(..)) " +
            "&& args(courseId, feedback)", argNames = "courseId, feedback")
    public void sendFeedbackForKeeperPointcut(Long courseId, CreateExplorerFeedbackDto feedback) {
    }

    @AfterReturning(pointcut = "sendFeedbackForKeeperPointcut(courseId, feedback)", returning = "result", argNames = "courseId, feedback, result")
    public void clearKeeperRatingCacheAfterFeedback(Long courseId, CreateExplorerFeedbackDto feedback, ExplorerFeedbackDto result) {
        clearKeeperRatingCache(feedback.getKeeperId());
    }

    @Async
    public void clearKeeperRatingCache(Long keeperId) {
        CompletableFuture.runAsync(() -> {
            Cache keeperRatingCache = Objects.requireNonNull(
                    cacheManager.getCache("keeperRatingCache")
            );
            Map<List<Long>, Double> nativeCache = getRatingNativeCache(keeperRatingCache);
            for (Map.Entry<List<Long>, Double> entry : nativeCache.entrySet()) {
                if (entry.getKey().contains(keeperId))
                    keeperRatingCache.evict(entry.getKey());
            }
        });
    }

    @Pointcut(value = "execution(* org.example.feedback.service.KeeperFeedbackService.sendFeedbackForExplorer(..)) " +
            "&& args(courseId, feedback)", argNames = "courseId, feedback")
    public void sendFeedbackForExplorerPointcut(Long courseId, CreateKeeperFeedbackDto feedback) {
    }

    @AfterReturning(pointcut = "sendFeedbackForExplorerPointcut(courseId, feedback)", returning = "result", argNames = "courseId,feedback,result")
    public void clearExplorerRatingCacheAfterFeedback(Long courseId, CreateKeeperFeedbackDto feedback, KeeperFeedbackDto result) {
        clearExplorerRatingCache(feedback.getExplorerId());
    }

    @Async
    public void clearExplorerRatingCache(Long explorerId) {
        CompletableFuture.runAsync(() -> {
            Cache explorerRatingCache = Objects.requireNonNull(
                    cacheManager.getCache("explorerRatingCache")
            );
            Map<List<Long>, Double> nativeCache = getRatingNativeCache(explorerRatingCache);
            for (Map.Entry<List<Long>, Double> entry : nativeCache.entrySet()) {
                if (entry.getKey().contains(explorerId))
                    explorerRatingCache.evict(entry.getKey());
            }
        });
    }

    private Map<List<Long>, Double> getRatingNativeCache(Cache cache) {
        return (Map<List<Long>, Double>) cache.getNativeCache();
    }
}
