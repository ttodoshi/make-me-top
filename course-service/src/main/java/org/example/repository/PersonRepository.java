package org.example.repository;

import org.example.dto.person.PersonDto;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface PersonRepository {
    Optional<PersonDto> findById(Integer personId);

    Map<Integer, PersonDto> findPeopleByPersonIdIn(List<Integer> personIds);
}
