package org.example.person.service;

import lombok.RequiredArgsConstructor;
import org.example.person.dto.keeper.CreateKeeperDto;
import org.example.person.dto.person.UpdatePersonDto;
import org.example.person.exception.classes.keeper.KeeperNotFoundException;
import org.example.person.model.Keeper;
import org.example.person.model.Person;
import org.example.person.repository.KeeperRepository;
import org.example.person.service.validator.KeeperValidatorService;
import org.springframework.beans.factory.annotation.Value;
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

    private final PersonService personService;
    private final KeeperValidatorService keeperValidatorService;

    @Value("${default-person-max-explorers-value}")
    private Integer DEFAULT_MAX_EXPLORERS_VALUE;

    @Cacheable(cacheNames = "keeperByIdCache", key = "#keeperId")
    @Transactional(readOnly = true)
    public Keeper findKeeperByKeeperId(Long keeperId) {
        return keeperRepository.findById(keeperId)
                .orElseThrow(KeeperNotFoundException::new);
    }

    @Cacheable(cacheNames = "keeperExistsByIdCache", key = "#keeperId")
    @Transactional(readOnly = true)
    public boolean keeperExistsById(Long keeperId) {
        return keeperRepository.existsById(keeperId);
    }

    @Cacheable(cacheNames = "keeperByPersonIdAndCourseIdCache", key = "{#personId, #courseId}")
    @Transactional(readOnly = true)
    public Keeper findKeeperByPersonIdAndCourseId(Long personId, Long courseId) {
        return keeperRepository.findKeeperByPersonIdAndCourseId(personId, courseId)
                .orElseThrow(KeeperNotFoundException::new);
    }

    @Cacheable(cacheNames = "keepersByPersonIdCache", key = "#personId")
    @Transactional(readOnly = true)
    public List<Keeper> findKeepersByPersonId(Long personId) {
        keeperValidatorService.validateKeepersByPersonIdRequest(personId);
        return keeperRepository.findKeepersByPersonId(personId);
    }

    @Cacheable(cacheNames = "keepersByPersonIdInCache", key = "#personIds")
    @Transactional(readOnly = true)
    public Map<Long, List<Keeper>> findKeepersByPersonIdIn(List<Long> personIds) {
        return keeperRepository.findKeepersByPersonIdIn(personIds)
                .stream()
                .collect(Collectors.groupingBy(
                        Keeper::getPersonId,
                        Collectors.toList()
                ));
    }

    @Cacheable(cacheNames = "keepersByCourseIdCache", key = "#courseId")
    @Transactional(readOnly = true)
    public List<Keeper> findKeepersByCourseId(Long courseId) {
        keeperValidatorService.validateKeepersByCourseIdRequest(courseId);
        return keeperRepository.findKeepersByCourseId(courseId);
    }

    @Cacheable(cacheNames = "keepersByKeeperIdInCache", key = "{#keeperIds}")
    @Transactional(readOnly = true)
    public Map<Long, Keeper> findKeepersByKeeperIdIn(List<Long> keeperIds) {
        return keeperRepository.findKeepersByKeeperIdIn(keeperIds)
                .stream()
                .collect(Collectors.toMap(
                        Keeper::getKeeperId,
                        k -> k
                ));
    }

    @Cacheable(cacheNames = "keepersByCourseIdInCache", key = "{#courseIds}")
    @Transactional(readOnly = true)
    public List<Keeper> findKeepersByCourseIdIn(List<Long> courseIds) {
        return keeperRepository.findKeepersByCourseIdIn(courseIds);
    }

    @Cacheable(cacheNames = "keepersByPersonIdAndCourseIdInCache", key = "{#personId, #courseIds}")
    @Transactional(readOnly = true)
    public List<Keeper> findKeepersByPersonIdAndCourseIdIn(Long personId, List<Long> courseIds) {
        return keeperRepository.findKeepersByPersonIdAndCourseIdIn(personId, courseIds);
    }

    @Caching(evict = {
            @CacheEvict(cacheNames = "keepersByPersonIdCache", key = "#keeper.personId"),
            @CacheEvict(cacheNames = "keeperExistsByIdCache", key = "#result"),
            @CacheEvict(cacheNames = "keepersByCourseIdCache", key = "#courseId"),
            @CacheEvict(cacheNames = {"keepersByPersonIdCache", "keepersByKeeperIdInCache", "keepersByPersonIdAndCourseIdInCache", "keepersByCourseIdInCache"}, allEntries = true),
    })
    @Transactional
    public Long setKeeperToCourse(Long courseId, CreateKeeperDto keeper) {
        keeperValidatorService.validateSetKeeperRequest(courseId, keeper);

        Person keeperPerson = personService.findPersonById(keeper.getPersonId());
        if (keeperPerson.getMaxExplorers().equals(0)) {
            personService.setMaxExplorersValueForPerson(
                    keeper.getPersonId(),
                    new UpdatePersonDto(
                            DEFAULT_MAX_EXPLORERS_VALUE
                    )
            );
        }

        return keeperRepository.save(
                new Keeper(courseId, keeper.getPersonId())
        ).getKeeperId();
    }

    @KafkaListener(topics = "deleteKeepersTopic", containerFactory = "deleteKeepersKafkaListenerContainerFactory")
    @CacheEvict(cacheNames = {"keeperByIdCache", "keeperExistsByIdCache", "keeperByPersonIdAndCourseIdCache", "keepersByPersonIdCache", "keepersByCourseIdCache", "keepersByKeeperIdInCache", "keepersByPersonIdAndCourseIdInCache", "allKeepersCache"}, allEntries = true)
    @Transactional
    public void deleteKeepersByCourseId(Long courseId) {
        keeperRepository.deleteKeepersByCourseId(courseId);
    }
}
