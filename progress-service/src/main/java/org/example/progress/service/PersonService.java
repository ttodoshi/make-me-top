package org.example.progress.service;

import org.example.grpc.PeopleService;

public interface PersonService {
    PeopleService.Person findPersonById(String authorizationHeader, Long personId);
}
