package org.example.galaxy.service;

import org.example.grpc.PeopleService;

public interface PersonService {
    PeopleService.Person findPersonById(Long personId);
}
