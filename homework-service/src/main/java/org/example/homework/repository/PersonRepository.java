package org.example.homework.repository;

import org.example.grpc.PeopleService;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface PersonRepository {
    Optional<PeopleService.Person> findById(Integer personId);

    Map<Integer, PeopleService.Person> findPeopleByPersonIdIn(List<Integer> personIds);
}
