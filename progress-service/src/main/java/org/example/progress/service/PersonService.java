package org.example.progress.service;

import org.example.grpc.PeopleService;

public interface PersonService {
    Long getAuthenticatedPersonId();

    PeopleService.Person getAuthenticatedPerson();

    PeopleService.Person findPersonById(Long personId);
}
