package org.example.repository;

import org.example.model.Person;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Map;

public interface PersonRepository extends JpaRepository<Person, Integer> {
    @Query(value = "SELECT * FROM course.person WHERE person_id = ?1", nativeQuery = true)
    Person getPersonById(Integer personID);
}
