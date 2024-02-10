package org.example.course.service;

import org.example.grpc.PeopleService;

import java.util.List;
import java.util.Map;

public interface PersonService {
    Map<Long, PeopleService.Person> findPeopleByPersonIdIn(String authorizationHeader, List<Long> personIds);
}
