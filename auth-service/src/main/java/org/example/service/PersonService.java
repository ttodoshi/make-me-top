package org.example.service;

import org.example.dto.LoginRequest;
import org.example.model.Person;

public interface PersonService {
    Person authenticatePerson(LoginRequest loginRequest);
}
