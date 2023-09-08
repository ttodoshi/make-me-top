package org.example.service;

import org.example.dto.LoginRequestDto;
import org.example.model.Person;

public interface PersonService {
    Person authenticatePerson(LoginRequestDto loginRequestDto);
}
