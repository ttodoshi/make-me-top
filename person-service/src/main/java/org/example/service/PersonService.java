package org.example.service;

import lombok.RequiredArgsConstructor;
import org.example.dto.event.PersonCreateEvent;
import org.example.dto.person.UpdatePersonDto;
import org.example.exception.classes.personEX.PersonNotFoundException;
import org.example.model.Person;
import org.example.repository.PersonRepository;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PersonService {
    private final PersonRepository personRepository;

    public Person findPersonById(Integer personId) {
        return personRepository.findById(personId)
                .orElseThrow(() -> new PersonNotFoundException(personId));
    }

    @KafkaListener(topics = "personTopic", containerFactory = "personKafkaListenerContainerFactory")
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

    @Transactional(readOnly = true)
    public Map<Integer, Person> findPeopleByPersonIdIn(List<Integer> personIds) {
        return personIds.stream()
                .map(this::findPersonById)
                .collect(Collectors.toMap(
                        Person::getPersonId,
                        p -> p
                ));
    }

    public Person setMaxExplorersValueForPerson(Integer personId, UpdatePersonDto personDto) {
        Person updatedPerson = personRepository.findById(personId)
                .orElseThrow(() -> new PersonNotFoundException(personId));
        updatedPerson.setMaxExplorers(personDto.getMaxExplorers());
        return personRepository.save(updatedPerson);
    }

    public void setDefaultExplorersValueForPerson(Integer personId) {
        setMaxExplorersValueForPerson(
                personId,
                new UpdatePersonDto(3)
        );
    }
}
