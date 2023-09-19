package org.example.repository;

import org.example.dto.PersonDto;

import java.util.Optional;

public interface PersonRepository {
    Optional<PersonDto> findById(Integer personId);
}
