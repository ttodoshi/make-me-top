package org.example.course.service;

import org.example.grpc.PeopleService;

public interface PersonService {
    Long getAuthenticatedPersonId();

    PeopleService.Person findPersonById(Long personId);
}
