package org.example.courseregistration.repository;

import org.example.grpc.PeopleService;

import java.util.Optional;

public interface PersonRepository {
    Optional<PeopleService.Person> findById(Long personId);
}
