package org.example.auth.service.implementations;

import lombok.RequiredArgsConstructor;
import org.example.auth.dto.mmtr.MmtrAuthResponseEmployeeDto;
import org.example.auth.exception.classes.person.PersonNotFoundException;
import org.example.auth.repository.PersonRepository;
import org.example.auth.service.PersonService;
import org.example.grpc.PeopleService;
import org.example.person.dto.event.PersonCreateEvent;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PersonServiceImpl implements PersonService {
    private final PersonRepository personRepository;

    private final KafkaTemplate<Long, Object> createPersonKafkaTemplate;

    @Override
    @Cacheable(cacheNames = "personByIdCache", key = "#personId")
    public PeopleService.Person findPersonById(Long personId) {
        return personRepository.findById(personId)
                .orElseThrow(PersonNotFoundException::new);
    }

    @Override
    public void savePersonIfNotExists(MmtrAuthResponseEmployeeDto employee) {
        Optional<PeopleService.Person> personOptional = personRepository.findById(employee.getEmployeeId());
        if (personOptional.isEmpty())
            createPerson(employee);
    }

    private void createPerson(MmtrAuthResponseEmployeeDto employee) {
        createPersonKafkaTemplate.send(
                "personTopic",
                employee.getEmployeeId(),
                PersonCreateEvent.builder()
                        .personId(employee.getEmployeeId())
                        .firstName(employee.getFirstName())
                        .lastName(employee.getLastName())
                        .patronymic(employee.getPatronymic())
                        .build()
        );
    }
}
