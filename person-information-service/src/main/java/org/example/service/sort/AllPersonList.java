package org.example.service.sort;

import lombok.AllArgsConstructor;
import org.example.dto.person.PersonWithGalaxyAndSystemsDto;

import java.util.List;

@AllArgsConstructor
public class AllPersonList implements PersonList {
    protected List<PersonWithGalaxyAndSystemsDto> keepers;

    @Override
    public List<PersonWithGalaxyAndSystemsDto> getPeople() {
        return keepers;
    }
}
