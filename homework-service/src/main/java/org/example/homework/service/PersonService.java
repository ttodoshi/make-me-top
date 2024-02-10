package org.example.homework.service;

import org.example.grpc.PeopleService;

import java.util.List;
import java.util.Map;

public interface PersonService {
    PeopleService.Person findPersonById(String authorizationHeader, Long personId);

    Map<Long, PeopleService.Person> findPeopleByPersonIdIn(String authorizationHeader, List<Long> personIds);
}
