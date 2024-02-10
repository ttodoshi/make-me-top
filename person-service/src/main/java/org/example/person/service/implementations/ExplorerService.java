package org.example.person.service.implementations;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.person.dto.event.ExplorerCreateEvent;
import org.example.person.exception.explorer.ExplorerNotFoundException;
import org.example.person.model.Explorer;
import org.example.person.repository.ExplorerRepository;
import org.example.person.service.api.validator.ExplorerValidatorService;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ExplorerService {
    private final ExplorerRepository explorerRepository;

    private final ExplorerValidatorService explorerValidatorService;

    @Transactional(readOnly = true)
    public Explorer findExplorerById(Long explorerId) {
        return explorerRepository.findById(explorerId)
                .orElseThrow(() -> {
                    log.warn("explorer by id {} not found", explorerId);
                    return new ExplorerNotFoundException(explorerId);
                });
    }

    @Cacheable(cacheNames = "explorerExistsByIdCache", key = "#explorerId")
    @Transactional(readOnly = true)
    public boolean explorerExistsById(Long explorerId) {
        return explorerRepository.existsById(explorerId);
    }

    @Cacheable(cacheNames = "explorerExistsByPersonIdAndCourseIdCache", key = "{#personId, #courseId}")
    @Transactional(readOnly = true)
    public boolean existsExplorerByPersonIdAndGroup_CourseId(Long personId, Long courseId) {
        return explorerRepository.existsExplorerByPersonIdAndGroup_CourseId(personId, courseId);
    }

    @Cacheable(cacheNames = "explorersByPersonIdAndCourseIdCache", key = "{#personId, #courseId}")
    @Transactional(readOnly = true)
    public Explorer findExplorerByPersonIdAndCourseId(Long personId, Long courseId) {
        return explorerRepository.findExplorerByPersonIdAndGroup_CourseId(personId, courseId)
                .orElseThrow(() -> {
                    log.warn("explorer by person id {} and course id {} not found", personId, courseId);
                    return new ExplorerNotFoundException();
                });
    }

    @Cacheable(cacheNames = "explorersByPersonIdCache", key = "#personId")
    @Transactional(readOnly = true)
    public List<Explorer> findExplorersByPersonId(Long personId) {
        explorerValidatorService.validateGetExplorersByPersonIdRequest(personId);
        return explorerRepository.findExplorersByPersonId(personId);
    }

    @Cacheable(cacheNames = "explorersByPersonIdInCache", key = "#personIds")
    @Transactional(readOnly = true)
    public Map<Long, List<Explorer>> findExplorersByPersonIdIn(List<Long> personIds) {
        return explorerRepository.findExplorersByPersonIdIn(personIds)
                .stream()
                .collect(Collectors.groupingBy(
                        Explorer::getPersonId,
                        Collectors.toList()
                ));
    }

    @Cacheable(cacheNames = "explorersByCourseIdCache", key = "#courseId")
    @Transactional(readOnly = true)
    public List<Explorer> findExplorersByCourseId(String authorizationHeader, Long courseId) {
        explorerValidatorService.validateGetExplorersByCourseIdRequest(authorizationHeader, courseId);
        return explorerRepository.findExplorersByGroup_CourseId(courseId);
    }

    @Cacheable(cacheNames = "explorersByExplorerIdInCache", key = "#explorerIds")
    @Transactional(readOnly = true)
    public Map<Long, Explorer> findExplorersByExplorerIdIn(List<Long> explorerIds) {
        return explorerRepository
                .findExplorersByExplorerIdIn(explorerIds)
                .stream()
                .collect(Collectors.toMap(
                        Explorer::getExplorerId,
                        e -> e
                ));
    }

    @Cacheable(cacheNames = "explorersByGroup_CourseIdInCache", key = "#courseIds")
    @Transactional(readOnly = true)
    public List<Explorer> findExplorersByGroup_CourseIdIn(List<Long> courseIds) {
        return explorerRepository.findExplorersByGroup_CourseIdIn(courseIds);
    }

    @Cacheable(cacheNames = "explorersByPersonIdAndGroup_CourseIdInCache", key = "{#personId, #courseIds}")
    @Transactional(readOnly = true)
    public List<Explorer> findExplorersByPersonIdAndGroup_CourseIdIn(Long personId, List<Long> courseIds) {
        return explorerRepository.findExplorersByPersonIdAndGroup_CourseIdIn(personId, courseIds);
    }

    @KafkaListener(topics = "createExplorerTopic", containerFactory = "createExplorerKafkaListenerContainerFactory")
    @Caching(evict = {
            @CacheEvict(cacheNames = "explorerExistsByIdCache", key = "#result.explorerId"),
            @CacheEvict(cacheNames = {"explorerExistsByPersonIdAndCourseIdCache", "explorersByPersonIdAndCourseIdCache", "explorersByPersonIdCache", "explorersByPersonIdInCache", "explorersByCourseIdCache", "explorersByExplorerIdInCache", "explorersByGroup_CourseIdInCache", "explorersByPersonIdAndGroup_CourseIdInCache"}, allEntries = true),
    })
    @Transactional
    public Explorer createExplorer(ExplorerCreateEvent explorer) {
        return explorerRepository.save(
                new Explorer(
                        explorer.getPersonId(),
                        explorer.getGroupId()
                )
        );
    }

    @Caching(evict = {
            @CacheEvict(cacheNames = {"explorerExistsByIdCache"}, key = "#explorerId"),
            @CacheEvict(cacheNames = {"explorerExistsByPersonIdAndCourseIdCache", "explorersByPersonIdAndCourseIdCache", "explorersByPersonIdCache", "explorersByCourseIdCache", "explorersByExplorerIdInCache", "explorersByGroup_CourseIdInCache", "explorersByPersonIdAndGroup_CourseIdInCache", "explorerGroupByIdCache", "explorerGroupsByKeeperIdCache", "explorerGroupsByKeeperIdInCache", "explorerGroupsByGroupIdInCache"}, allEntries = true),
    })
    @Transactional
    public void deleteExplorerById(String authorizationHeader, Authentication authentication, Long explorerId) {
        explorerValidatorService.validateDeleteExplorerByIdRequest(authorizationHeader, authentication, explorerId);
        explorerRepository.deleteById(explorerId);
    }
}
