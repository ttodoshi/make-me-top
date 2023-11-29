package org.example.homework.service;

import org.example.grpc.PeopleService;

public interface PersonService {
    Long getAuthenticatedPersonId();

    PeopleService.Person getAuthenticatedPerson();

    PeopleService.Person findPersonById(Long personId);
}
