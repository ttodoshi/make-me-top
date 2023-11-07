package org.example.service.implementations;

import lombok.RequiredArgsConstructor;
import org.example.dto.event.PersonCreateEvent;
import org.example.dto.mmtr.MmtrAuthResponseEmployeeDto;
import org.example.exception.classes.personEX.PersonNotFoundException;
import org.example.grpc.PeopleService;
import org.example.repository.PersonRepository;
import org.example.service.PersonService;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PersonServiceImpl implements PersonService {
    private final PersonRepository personRepository;

    private final KafkaTemplate<Integer, Object> kafkaTemplate;

    @Override
    @Cacheable(cacheNames = "personByIdCache", key = "#personId")
    public PeopleService.Person findPersonById(Integer personId) {
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
        kafkaTemplate.send(
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
