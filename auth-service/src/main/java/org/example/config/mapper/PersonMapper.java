package org.example.config.mapper;

import org.example.dto.AuthResponseUser;
import org.example.model.Person;
import org.springframework.context.annotation.Configuration;

import java.util.Date;

@Configuration
public class PersonMapper {

    public Person UserAuthResponseToPerson(AuthResponseUser authResponseUser) {
        return new Person(
                authResponseUser.getEmployeeId(),
                authResponseUser.getFirstName(),
                authResponseUser.getLastName(),
                authResponseUser.getPatronymic(),
                new Date()
        );
    }
}
