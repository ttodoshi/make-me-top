package org.example.homework.service;

import org.example.grpc.PeopleService;

public interface PersonService {
    Integer getAuthenticatedPersonId();

    PeopleService.Person getAuthenticatedPerson();

    PeopleService.Person findPersonById(Integer personId);
}
