package org.example.person.aspect;

import lombok.RequiredArgsConstructor;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.example.person.dto.person.UpdatePersonDto;
import org.example.person.model.Person;
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

    @Pointcut(value = "execution(* org.example.person.service.PersonService.setMaxExplorersValueForPerson(..)) " +
            "&& args(personId, person)", argNames = "personId, person")
    public void setMaxExplorersValueForPersonPointcut(Long personId, UpdatePersonDto person) {
    }

    @AfterReturning(pointcut = "setMaxExplorersValueForPersonPointcut(personId, person)", argNames = "personId, person")
    public void clearKeeperRatingCacheAfterFeedback(Long personId, UpdatePersonDto person) {
        clearPeopleByPersonIdInCache(personId);
    }

    @Async
    @SuppressWarnings("unchecked")
    public void clearPeopleByPersonIdInCache(Long personId) {
        CompletableFuture.runAsync(() -> {
            Cache peopleByPersonIdInCache = cacheManager.getCache("peopleByPersonIdInCache");
            Map<List<Long>, Map<Long, Person>> nativeCache = (Map<List<Long>, Map<Long, Person>>)
                    Objects.requireNonNull(peopleByPersonIdInCache).getNativeCache();
            for (Map.Entry<List<Long>, Map<Long, Person>> entry : nativeCache.entrySet()) {
                if (entry.getKey().contains(personId))
                    peopleByPersonIdInCache.evict(entry.getKey());
            }
        });
    }
}
