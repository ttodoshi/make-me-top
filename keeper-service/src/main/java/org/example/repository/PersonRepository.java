package org.example.repository;

import org.example.dto.person.PersonDto;

import java.util.Optional;

public interface PersonRepository {
    Optional<PersonDto> findById(Integer personId);

    Boolean existsById(Integer personId);
}
