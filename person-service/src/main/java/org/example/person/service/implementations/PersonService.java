package org.example.person.service.implementations;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.person.dto.event.PersonSaveEvent;
import org.example.person.dto.person.UpdatePersonDto;
import org.example.person.exception.person.PersonNotFoundException;
import org.example.person.model.Person;
import org.example.person.repository.PersonRepository;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class PersonService {
    private final PersonRepository personRepository;

    @Cacheable(cacheNames = "personByIdCache", key = "#personId")
    @Transactional(readOnly = true)
    public Person findPersonById(Long personId) {
        return personRepository.findById(personId)
                .orElseThrow(() -> {
                    log.warn("person by id {} not found", personId);
                    return new PersonNotFoundException(personId);
                });
    }

    @Cacheable(cacheNames = "personExistsByIdCache", key = "#personId")
    @Transactional(readOnly = true)
    public boolean personExistsById(Long personId) {
        return personRepository.existsById(personId);
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

    @KafkaListener(topics = "updatePersonTopic", containerFactory = "savePersonKafkaListenerContainerFactory")
    @CacheEvict(cacheNames = {"personExistsByIdCache", "personByIdCache"}, key = "#person.personId")
    @Transactional
    public void savePerson(PersonSaveEvent person) {
        personRepository.findById(person.getPersonId())
                .ifPresentOrElse(
                        p -> {
                            p.setFirstName(person.getFirstName());
                            p.setLastName(person.getLastName());
                            p.setPatronymic(person.getPatronymic());
                            p.setEmail(person.getEmail());
                            p.setPhoneNumber(person.getPhoneNumber());
                            p.setSkype(person.getSkype());
                            p.setTelegram(person.getTelegram());
                            p.setIsVisiblePrivateData(person.getIsVisiblePrivateData());
                        }, () -> personRepository.save(
                                new Person(
                                        person.getPersonId(),
                                        person.getFirstName(),
                                        person.getLastName(),
                                        person.getPatronymic(),
                                        person.getEmail(),
                                        person.getPhoneNumber(),
                                        person.getSkype(),
                                        person.getTelegram(),
                                        person.getIsVisiblePrivateData()
                                )
                        ));
    }

    @CacheEvict(cacheNames = "personByIdCache", key = "#personId")
    @Transactional
    public void setMaxExplorersValueForPerson(Long personId, UpdatePersonDto person) {
        Person updatedPerson = personRepository.findById(personId)
                .orElseThrow(() -> {
                    log.warn("person by id {} not found", personId);
                    return new PersonNotFoundException(personId);
                });

        updatedPerson.setMaxExplorers(person.getMaxExplorers());
    }
}
