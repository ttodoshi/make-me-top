package org.example.person.service;

import lombok.RequiredArgsConstructor;
import org.example.person.dto.keeper.CreateKeeperDto;
import org.example.person.exception.classes.keeper.KeeperNotFoundException;
import org.example.person.model.Keeper;
import org.example.person.repository.ExplorerGroupRepository;
import org.example.person.repository.KeeperRepository;
import org.example.person.service.validator.KeeperValidatorService;
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
public class KeeperService {
    private final KeeperRepository keeperRepository;
    private final ExplorerGroupRepository explorerGroupRepository;

    private final ExplorerService explorerService;
    private final PersonService personService;
    private final KeeperValidatorService keeperValidatorService;

    @Cacheable(cacheNames = "keeperByIdCache", key = "#keeperId")
    @Transactional(readOnly = true)
    public Keeper findKeeperByKeeperId(Integer keeperId) {
        return keeperRepository.findById(keeperId)
                .orElseThrow(KeeperNotFoundException::new);
    }

    @Cacheable(cacheNames = "keeperExistsByIdCache", key = "#keeperId")
    @Transactional(readOnly = true)
    public boolean keeperExistsById(Integer keeperId) {
        return keeperRepository.existsById(keeperId);
    }

    @Cacheable(cacheNames = "keeperByPersonIdAndCourseIdCache", key = "{#personId, #courseId}")
    @Transactional(readOnly = true)
    public Keeper findKeeperByPersonIdAndCourseId(Integer personId, Integer courseId) {
        return keeperRepository.findKeeperByPersonIdAndCourseId(personId, courseId)
                .orElseThrow(KeeperNotFoundException::new);
    }

    @Cacheable(cacheNames = "keepersByPersonIdCache", key = "#personId")
    @Transactional(readOnly = true)
    public List<Keeper> findKeepersByPersonId(Integer personId) {
        keeperValidatorService.validateKeepersByPersonIdRequest(personId);
        return keeperRepository.findKeepersByPersonId(personId);
    }

    @Cacheable(cacheNames = "keepersByCourseIdCache", key = "#courseId")
    @Transactional(readOnly = true)
    public List<Keeper> findKeepersByCourseId(Integer courseId) {
        keeperValidatorService.validateKeepersByCourseIdRequest(courseId);
        return keeperRepository.findKeepersByCourseId(courseId);
    }

    @Transactional(readOnly = true)
    public Map<Integer, Keeper> findKeepersByKeeperIdIn(List<Integer> keeperIds) {
        return keeperRepository.findKeepersByKeeperIdIn(keeperIds)
                .stream()
                .collect(
                        Collectors.toMap(
                                Keeper::getKeeperId,
                                k -> k
                        )
                );
    }

    @Transactional(readOnly = true)
    public List<Keeper> findKeepersByPersonIdAndCourseIdIn(Integer personId, List<Integer> courseIds) {
        return keeperRepository.findKeepersByPersonIdAndCourseIdIn(personId, courseIds);
    }

    @Transactional(readOnly = true)
    public List<Keeper> findAllKeepers() {
        return keeperRepository.findAll();
    }

    @Caching(evict = {
            @CacheEvict(cacheNames = "keepersByPersonIdCache", key = "#createKeeper.personId"),
            @CacheEvict(cacheNames = "keeperExistsByIdCache", key = "#result.keeperId"),
            @CacheEvict(cacheNames = "keepersByCourseIdCache", key = "#courseId")
    })
    @Transactional
    public Keeper setKeeperToCourse(Integer courseId, CreateKeeperDto createKeeper) {
        keeperValidatorService.validateSetKeeperRequest(courseId, createKeeper);
        personService.setDefaultExplorersValueForPerson(createKeeper.getPersonId());
        return keeperRepository.save(
                new Keeper(courseId, createKeeper.getPersonId())
        );
    }

    @KafkaListener(topics = "deleteKeepersTopic", containerFactory = "deleteKeepersKafkaListenerContainerFactory")
    @Transactional
    public void deleteKeepersByCourseId(Integer courseId) {
        explorerGroupRepository.findExplorerGroupsByKeeperIdIn(
                keeperRepository.findKeepersByCourseId(
                        courseId
                ).stream().map(Keeper::getKeeperId).collect(Collectors.toList())
        ).forEach(g -> g.getExplorers().forEach(e ->
                explorerService.deleteExplorerRelatedData(e.getExplorerId())));
        keeperRepository.deleteKeepersByCourseId(courseId);
    }
}
