package org.example.feedback.aspect;

import lombok.RequiredArgsConstructor;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.example.feedback.dto.feedback.CreateExplorerFeedbackDto;
import org.example.feedback.dto.feedback.CreateKeeperFeedbackDto;
import org.example.feedback.exception.feedback.OfferNotFoundException;
import org.example.feedback.repository.ExplorerFeedbackOfferRepository;
import org.example.feedback.repository.KeeperFeedbackOfferRepository;
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
    private final ExplorerFeedbackOfferRepository explorerFeedbackOfferRepository;
    private final KeeperFeedbackOfferRepository keeperFeedbackOfferRepository;

    @Pointcut(value = "execution(* org.example.feedback.service.ExplorerFeedbackService.sendFeedbackForKeeper(..)) " +
            "&& args(authorizationHeader, authenticatedPersonId, feedback)", argNames = "authorizationHeader, authenticatedPersonId, feedback")
    public void sendFeedbackForKeeperPointcut(String authorizationHeader, Long authenticatedPersonId, CreateExplorerFeedbackDto feedback) {
    }

    @AfterReturning(pointcut = "sendFeedbackForKeeperPointcut(authorizationHeader, authenticatedPersonId, feedback)", argNames = "authorizationHeader, authenticatedPersonId, feedback")
    public void clearKeeperRatingCacheAfterFeedback(String authorizationHeader, Long authenticatedPersonId, CreateExplorerFeedbackDto feedback) {
        clearKeeperRatingCache(
                explorerFeedbackOfferRepository.findById(
                                feedback.getExplorerId()
                        ).orElseThrow(() -> new OfferNotFoundException(feedback.getExplorerId()))
                        .getKeeperId()
        );
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
            "&& args(authorizationHeader, authenticatedPersonId, feedback)", argNames = "authorizationHeader, authenticatedPersonId, feedback")
    public void sendFeedbackForExplorerPointcut(String authorizationHeader, Long authenticatedPersonId, CreateKeeperFeedbackDto feedback) {
    }

    @AfterReturning(pointcut = "sendFeedbackForExplorerPointcut(authorizationHeader, authenticatedPersonId, feedback)", argNames = "authorizationHeader, authenticatedPersonId, feedback")
    public void clearExplorerRatingCacheAfterFeedback(String authorizationHeader, Long authenticatedPersonId, CreateKeeperFeedbackDto feedback) {
        clearExplorerRatingCache(
                keeperFeedbackOfferRepository.findById(
                                feedback.getExplorerId()
                        ).orElseThrow(() -> new OfferNotFoundException(feedback.getExplorerId()))
                        .getExplorerId()
        );
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

    @SuppressWarnings("unchecked")
    private Map<List<Long>, Double> getRatingNativeCache(Cache cache) {
        return (Map<List<Long>, Double>) cache.getNativeCache();
    }
}
