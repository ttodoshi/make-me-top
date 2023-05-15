package org.example.utils;

import org.example.model.Person;
import org.example.model.UserAuthResponse;
import org.springframework.context.annotation.Configuration;

@Configuration
public class PersonMapper {

    Person person;

    public Person UserAuthResponseToPerson(UserAuthResponse userAuthResponse) {
        person = new Person();
        person.setPersonId(userAuthResponse.getEmployeeId());
        person.setFirstName(userAuthResponse.getFirstName());
        person.setLastName(userAuthResponse.getLastName());
        person.setPatronymic(userAuthResponse.getPatronymic());
        return person;
    }
}
