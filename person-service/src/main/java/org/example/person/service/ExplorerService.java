package org.example.person.service;

import lombok.RequiredArgsConstructor;
import org.example.person.dto.event.ExplorerCreateEvent;
import org.example.person.exception.classes.explorer.ExplorerNotFoundException;
import org.example.person.model.Explorer;
import org.example.person.repository.ExplorerRepository;
import org.example.person.service.validator.ExplorerValidatorService;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ExplorerService {
    private final ExplorerRepository explorerRepository;

    private final ExplorerValidatorService explorerValidatorService;

    @Transactional(readOnly = true)
    public Explorer findExplorerById(Long explorerId) {
        return explorerRepository.findById(explorerId)
                .orElseThrow(ExplorerNotFoundException::new);
    }

    @Cacheable(cacheNames = "explorerExistsByIdCache", key = "#explorerId")
    @Transactional(readOnly = true)
    public boolean explorerExistsById(Long explorerId) {
        return explorerRepository.existsById(explorerId);
    }

    @Cacheable(cacheNames = "explorersByPersonIdAndCourseIdCache", key = "{#personId, #courseId}")
    @Transactional(readOnly = true)
    public Explorer findExplorerByPersonIdAndCourseId(Long personId, Long courseId) {
        return explorerRepository.findExplorerByPersonIdAndGroup_CourseId(personId, courseId)
                .orElseThrow(ExplorerNotFoundException::new);
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
    public List<Explorer> findExplorersByCourseId(Long courseId) {
        explorerValidatorService.validateGetExplorersByCourseIdRequest(courseId);
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

    @KafkaListener(topics = "explorerTopic", containerFactory = "createExplorerKafkaListenerContainerFactory")
    @Caching(evict = {
            @CacheEvict(cacheNames = "explorerExistsByIdCache", key = "#result.explorerId"),
            @CacheEvict(cacheNames = {"explorersByPersonIdAndCourseIdCache", "explorersByPersonIdCache", "explorersByPersonIdInCache", "explorersByCourseIdCache", "explorersByExplorerIdInCache", "explorersByGroup_CourseIdInCache", "explorersByPersonIdAndGroup_CourseIdInCache"}, allEntries = true),
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
            @CacheEvict(cacheNames = {"explorersByPersonIdAndCourseIdCache", "explorersByPersonIdCache", "explorersByCourseIdCache", "explorersByExplorerIdInCache", "explorersByGroup_CourseIdInCache", "explorersByPersonIdAndGroup_CourseIdInCache", "explorerGroupByIdCache", "explorerGroupsByKeeperIdCache", "explorerGroupsByKeeperIdInCache", "explorerGroupsByGroupIdInCache"}, allEntries = true),
    })
    @Transactional
    public void deleteExplorerById(Long explorerId) {
        explorerValidatorService.validateDeleteExplorerByIdRequest(explorerId);
        explorerRepository.deleteById(explorerId);
    }
}
