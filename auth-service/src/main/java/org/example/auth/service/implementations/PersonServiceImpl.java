package org.example.auth.service.implementations;

import lombok.RequiredArgsConstructor;
import org.example.auth.dto.mmtr.MmtrAuthResponseEmployeeDto;
import org.example.auth.exception.classes.person.PersonNotFoundException;
import org.example.auth.repository.PersonRepository;
import org.example.auth.service.PersonService;
import org.example.grpc.PeopleService;
import org.example.person.dto.event.PersonSaveEvent;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PersonServiceImpl implements PersonService {
    private final PersonRepository personRepository;

    private final KafkaTemplate<Long, Object> savePersonKafkaTemplate;

    @Override
    @Cacheable(cacheNames = "personByIdCache", key = "#personId")
    public PeopleService.Person findPersonById(Long personId) {
        return personRepository.findById(personId)
                .orElseThrow(PersonNotFoundException::new);
    }

    @Override
    @CacheEvict(cacheNames = "personByIdCache", key = "#employee.employeeId")
    public void savePerson(MmtrAuthResponseEmployeeDto employee) {
        savePersonKafkaTemplate.send(
                "personTopic",
                employee.getEmployeeId(),
                PersonSaveEvent.builder()
                        .personId(employee.getEmployeeId())
                        .firstName(employee.getFirstName())
                        .lastName(employee.getLastName())
                        .patronymic(employee.getPatronymic())
                        .email(employee.getEmail())
                        .phoneNumber(employee.getPhoneNumber())
                        .skype(employee.getSkype())
                        .telegram(employee.getTelegram())
                        .isVisiblePrivateData(employee.getIsVisiblePrivateData())
                        .build()
        );
    }
}
