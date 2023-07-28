package org.example.config.mapper;

import org.example.dto.AuthResponseEmployee;
import org.example.model.Person;
import org.springframework.context.annotation.Configuration;

import java.util.Date;

@Configuration
public class PersonMapper {

    public Person UserAuthResponseToPerson(AuthResponseEmployee authResponseEmployee) {
        return new Person(
                authResponseEmployee.getEmployeeId(),
                authResponseEmployee.getFirstName(),
                authResponseEmployee.getLastName(),
                authResponseEmployee.getPatronymic(),
                new Date()
        );
    }
}
