package org.example.auth.service.implementations;

import lombok.RequiredArgsConstructor;
import org.example.auth.dto.mmtr.MmtrAuthResponseEmployeeDto;
import org.example.auth.service.PersonService;
import org.example.person.dto.event.PersonSaveEvent;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PersonServiceImpl implements PersonService {
    private final KafkaTemplate<Long, Object> savePersonKafkaTemplate;

    @Override
    public void savePerson(MmtrAuthResponseEmployeeDto employee) {
        savePersonKafkaTemplate.send(
                "updatePersonTopic",
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
