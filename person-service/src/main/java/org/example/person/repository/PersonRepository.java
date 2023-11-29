package org.example.person.repository;

import org.example.person.model.Person;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PersonRepository extends JpaRepository<Person, Long> {
    List<Person> findPeopleByPersonIdIn(List<Long> personIds);
}
