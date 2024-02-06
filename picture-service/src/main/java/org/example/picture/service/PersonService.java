package org.example.picture.service;

import org.example.grpc.PeopleService;

public interface PersonService {
    PeopleService.Person findPersonById(Long personId);
}
