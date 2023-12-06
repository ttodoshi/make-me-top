package org.example.person.service;

import lombok.RequiredArgsConstructor;
import org.example.person.dto.event.PersonCreateEvent;
import org.example.person.dto.person.UpdatePersonDto;
import org.example.person.exception.classes.person.PersonNotFoundException;
import org.example.person.model.Person;
import org.example.person.repository.PersonRepository;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PersonService {
    private final PersonRepository personRepository;

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
    @CacheEvict(cacheNames = "personExistsByIdCache", key = "#person.personId")
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

    @CacheEvict(cacheNames = "personByIdCache", key = "#personId")
    @Transactional
    public void setMaxExplorersValueForPerson(Long personId, UpdatePersonDto person) {
        Person updatedPerson = personRepository.findById(personId)
                .orElseThrow(() -> new PersonNotFoundException(personId));

        updatedPerson.setMaxExplorers(person.getMaxExplorers());
        personRepository.save(updatedPerson);
    }
}
