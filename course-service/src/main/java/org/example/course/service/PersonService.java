package org.example.course.service;

import org.example.grpc.PeopleService;

public interface PersonService {
    Integer getAuthenticatedPersonId();

    PeopleService.Person findPersonById(Integer personId);
}
