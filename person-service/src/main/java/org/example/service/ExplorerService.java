package org.example.service;

import org.example.dto.event.ExplorerCreateEvent;
import org.example.dto.explorer.ExplorerBasicInfoDto;
import org.example.dto.message.MessageDto;
import org.example.exception.classes.explorerEX.ExplorerNotFoundException;
import org.example.model.Explorer;
import org.example.repository.ExplorerRepository;
import org.example.service.validator.ExplorerValidatorService;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ExplorerService {
    private final ExplorerRepository explorerRepository;

    private final ExplorerValidatorService explorerValidatorService;

    private final KafkaTemplate<Integer, Integer> deleteProgressAndMarkByExplorerIdKafkaTemplate;
    private final KafkaTemplate<Integer, Integer> deleteFeedbackByExplorerIdKafkaTemplate;

    public ExplorerService(ExplorerRepository explorerRepository, ExplorerValidatorService explorerValidatorService,
                           @Qualifier("deleteProgressAndMarkByExplorerIdKafkaTemplate") KafkaTemplate<Integer, Integer> deleteProgressAndMarkByExplorerIdKafkaTemplate,
                           @Qualifier("deleteFeedbackByExplorerIdKafkaTemplate") KafkaTemplate<Integer, Integer> deleteFeedbackByExplorerIdKafkaTemplate) {
        this.explorerRepository = explorerRepository;
        this.explorerValidatorService = explorerValidatorService;
        this.deleteProgressAndMarkByExplorerIdKafkaTemplate = deleteProgressAndMarkByExplorerIdKafkaTemplate;
        this.deleteFeedbackByExplorerIdKafkaTemplate = deleteFeedbackByExplorerIdKafkaTemplate;
    }

    @Cacheable(cacheNames = "explorerByIdCache", key = "#explorerId")
    @Transactional(readOnly = true)
    public Explorer findExplorerById(Integer explorerId) {
        return explorerRepository.findById(explorerId)
                .orElseThrow(ExplorerNotFoundException::new);
    }

    @Cacheable(cacheNames = "explorerExistsByIdCache", key = "#explorerId")
    @Transactional(readOnly = true)
    public boolean explorerExistsById(Integer explorerId) {
        return explorerRepository.existsById(explorerId);
    }

    @Transactional(readOnly = true)
    public Explorer findExplorerByPersonIdAndCourseId(Integer personId, Integer courseId) {
        return explorerRepository.findExplorerByPersonIdAndGroup_CourseId(personId, courseId)
                .orElseThrow(ExplorerNotFoundException::new);
    }

    @Transactional(readOnly = true)
    public List<Explorer> findExplorersByPersonId(Integer personId) {
        explorerValidatorService.validateGetExplorersByPersonIdRequest(personId);
        return explorerRepository.findExplorersByPersonId(personId);
    }

    @Transactional(readOnly = true)
    public List<Explorer> findExplorersByCourseId(Integer courseId) {
        explorerValidatorService.validateGetExplorersByCourseIdRequest(courseId);
        return explorerRepository.findExplorersByGroup_CourseId(courseId);
    }

    @Transactional(readOnly = true)
    public List<Explorer> findExplorersByExplorerIdIn(List<Integer> explorerIds) {
        return explorerRepository.findExplorersByExplorerIdIn(explorerIds);
    }

    @Cacheable(cacheNames = "explorersByGroup_CourseIdInCache")
    @Transactional(readOnly = true)
    public List<Explorer> findExplorersByGroup_CourseIdIn(List<Integer> courseIds) {
        return explorerRepository.findExplorersByGroup_CourseIdIn(courseIds);
    }

    @Transactional(readOnly = true)
    public List<Explorer> findExplorersByPersonIdAndGroup_CourseIdIn(Integer personId, List<Integer> courseIds) {
        return explorerRepository.findExplorersByPersonIdAndGroup_CourseIdIn(personId, courseIds);
    }

    @Cacheable(cacheNames = "allExplorersCache")
    @Transactional(readOnly = true)
    public List<ExplorerBasicInfoDto> findAllExplorers() {
        return explorerRepository.findAll()
                .stream()
                .map(e -> new ExplorerBasicInfoDto(
                        e.getPersonId(),
                        e.getPerson().getFirstName(),
                        e.getPerson().getLastName(),
                        e.getPerson().getPatronymic(),
                        e.getExplorerId(),
                        e.getGroup().getCourseId(),
                        e.getGroupId()
                )).collect(Collectors.toList());
    }

    @KafkaListener(topics = "explorerTopic", containerFactory = "createExplorerKafkaListenerContainerFactory")
    @Caching(evict = {
            @CacheEvict(cacheNames = "explorerExistsByIdCache", key = "#result.explorerId"),
            @CacheEvict(cacheNames = "explorersByGroup_CourseIdInCache", allEntries = true),
            @CacheEvict(cacheNames = "allExplorersCache", allEntries = true),
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
            @CacheEvict(cacheNames = {"explorerByIdCache", "explorerExistsByIdCache"}, key = "#explorerId"),
            @CacheEvict(cacheNames = "explorersByGroup_CourseIdInCache", allEntries = true),
            @CacheEvict(cacheNames = "allExplorersCache", allEntries = true),
    })
    @Transactional
    public MessageDto deleteExplorerById(Integer explorerId) {
        explorerValidatorService.validateDeleteExplorerByIdRequest(explorerId);
        explorerRepository.deleteById(explorerId);
        deleteExplorerRelatedData(explorerId);
        return new MessageDto("Вы ушли с курса");
    }

    public void deleteExplorerRelatedData(Integer explorerId) {
        deleteProgressAndMarkByExplorerIdKafkaTemplate.send(
                "deleteProgressAndMarkTopic",
                explorerId);
        deleteFeedbackByExplorerIdKafkaTemplate.send(
                "deleteFeedbackTopic",
                explorerId
        );
    }
}
