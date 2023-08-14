package org.example.service.sort;

import org.example.dto.person.PersonWithRatingAndGalaxyDTO;

import java.util.List;

public interface PersonList {
    List<PersonWithRatingAndGalaxyDTO> getPeople();
}
