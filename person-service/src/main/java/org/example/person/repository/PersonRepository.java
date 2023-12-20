package org.example.person.repository;

import org.example.person.model.Person;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface PersonRepository extends JpaRepository<Person, Long> {
    List<Person> findPeopleByPersonIdIn(List<Long> personIds);

    @Query("SELECT DISTINCT p FROM Person p\n" +
            "JOIN Keeper k ON k.personId = p.personId")
    Page<Person> findKeeperPeople(Pageable pageable);

    @Query("SELECT DISTINCT p FROM Person p\n" +
            "JOIN Explorer e ON e.personId = p.personId")
    Page<Person> findExplorerPeople(Pageable pageable);
}
