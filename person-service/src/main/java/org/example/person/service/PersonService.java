package org.example.person.service;

import lombok.RequiredArgsConstructor;
import org.example.person.dto.event.PersonCreateEvent;
import org.example.person.dto.person.UpdatePersonDto;
import org.example.person.exception.classes.person.PersonNotFoundException;
import org.example.person.model.Person;
import org.example.person.repository.PersonRepository;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PersonService {
    private final PersonRepository personRepository;

    private final CacheManager cacheManager;

    public Long getAuthenticatedPersonId() {
        Person authenticatedPerson = (Person) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return authenticatedPerson.getPersonId();
    }

    public Person getAuthenticatedPerson() {
        return (Person) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    }

    @Cacheable(cacheNames = "personByIdCache", key = "#personId")
    @Transactional(readOnly = true)
    public Person findPersonById(Long personId) {
        return personRepository.findById(personId)
                .orElseThrow(() -> new PersonNotFoundException(personId));
    }

    @Cacheable(cacheNames = "personExistsByIdCache", key = "#personId")
    @Transactional(readOnly = true)
    public boolean personExistsById(Long personId) {
        return personRepository.existsById(personId);
    }

    @KafkaListener(topics = "personTopic", containerFactory = "createPersonKafkaListenerContainerFactory")
    @Transactional
    public void savePerson(PersonCreateEvent person) {
        personRepository.save(
                new Person(
                        person.getPersonId(),
                        person.getFirstName(),
                        person.getLastName(),
                        person.getPatronymic(),
                        0
                )
        );
    }

    @Cacheable(cacheNames = "peopleByPersonIdInCache", key = "#personIds")
    @Transactional(readOnly = true)
    public Map<Long, Person> findPeopleByPersonIdIn(List<Long> personIds) {
        return personRepository.findPeopleByPersonIdIn(personIds)
                .stream()
                .collect(Collectors.toMap(
                        Person::getPersonId,
                        p -> p
                ));
    }

    @Transactional
    @CachePut(cacheNames = "personByIdCache", key = "#personId")
    public Person setMaxExplorersValueForPerson(Long personId, UpdatePersonDto personDto) {
        Person updatedPerson = personRepository.findById(personId)
                .orElseThrow(() -> new PersonNotFoundException(personId));
        updatedPerson.setMaxExplorers(personDto.getMaxExplorers());
        clearPeopleByPersonIdInCache(personId);
        return personRepository.save(updatedPerson);
    }

    @Async
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

    @Transactional
    public void setDefaultExplorersValueForPerson(Long personId) {
        setMaxExplorersValueForPerson(
                personId,
                new UpdatePersonDto(3)
        );
    }
}
