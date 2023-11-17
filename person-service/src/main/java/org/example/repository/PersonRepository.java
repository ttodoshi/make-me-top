package org.example.repository;

import org.example.model.Person;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PersonRepository extends JpaRepository<Person, Integer> {
    List<Person> findPeopleByPersonIdIn(List<Integer> personIds);
}
