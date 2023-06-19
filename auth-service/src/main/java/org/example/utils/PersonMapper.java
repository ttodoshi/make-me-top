package org.example.utils;

import org.example.model.Person;
import org.example.model.Role;
import org.example.dto.UserAuthResponse;
import org.springframework.context.annotation.Configuration;

@Configuration
public class PersonMapper {

    public Person UserAuthResponseToPerson(UserAuthResponse userAuthResponse) {
        return new Person(
                userAuthResponse.getEmployeeId(),
                Role.EXPLORER,
                userAuthResponse.getFirstName(),
                userAuthResponse.getLastName(),
                userAuthResponse.getPatronymic()
        );
    }
}
