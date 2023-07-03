package org.example.config.mapper;

import org.example.dto.UserAuthResponse;
import org.example.model.Person;
import org.springframework.context.annotation.Configuration;

import java.util.Collections;
import java.util.Date;

@Configuration
public class PersonMapper {

    public Person UserAuthResponseToPerson(UserAuthResponse userAuthResponse) {
        return new Person(
                userAuthResponse.getEmployeeId(),
                userAuthResponse.getFirstName(),
                userAuthResponse.getLastName(),
                userAuthResponse.getPatronymic(),
                new Date()
        );
    }
}
