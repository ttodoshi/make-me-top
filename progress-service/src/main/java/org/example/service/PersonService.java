package org.example.service;

import org.example.dto.PersonDto;

public interface PersonService {
    Integer getAuthenticatedPersonId();
    PersonDto getAuthenticatedPerson();
}
