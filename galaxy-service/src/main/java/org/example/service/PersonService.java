package org.example.service;

import org.example.grpc.PeopleService;

public interface PersonService {
    PeopleService.Person findPersonById(Integer personId);
}
