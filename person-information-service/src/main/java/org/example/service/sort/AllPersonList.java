package org.example.service.sort;

import lombok.AllArgsConstructor;
import org.example.dto.person.PersonWithRatingAndGalaxyDTO;

import java.util.List;

@AllArgsConstructor
public class AllPersonList implements PersonList {
    protected List<PersonWithRatingAndGalaxyDTO> keepers;

    @Override
    public List<PersonWithRatingAndGalaxyDTO> getPeople() {
        return keepers;
    }
}
