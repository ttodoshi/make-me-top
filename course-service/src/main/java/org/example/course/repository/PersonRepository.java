package org.example.course.repository;

import org.example.grpc.PeopleService;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface PersonRepository {
    Optional<PeopleService.Person> findById(Long personId);

    Map<Long, PeopleService.Person> findPeopleByPersonIdIn(List<Long> personIds);
}
