package org.example.service;

import org.example.dto.person.PersonDto;

public interface PersonService {
    PersonDto findPersonById(Integer personId);
}
