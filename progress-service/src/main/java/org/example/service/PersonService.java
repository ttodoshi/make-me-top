package org.example.service;

import org.example.dto.PersonDto;

public interface PersonService {
    PersonDto getAuthenticatedPerson();

    PersonDto findPersonById(Integer personId);
}
