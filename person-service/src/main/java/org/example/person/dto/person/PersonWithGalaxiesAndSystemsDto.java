package org.example.person.dto.person;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.example.person.dto.galaxy.GalaxyDto;

import java.util.List;

@Data
@EqualsAndHashCode(callSuper = true)
public class PersonWithGalaxiesAndSystemsDto extends PersonWithSystemsDto {
    private List<GalaxyDto> galaxies;

    public PersonWithGalaxiesAndSystemsDto(PersonWithSystemsDto person, List<GalaxyDto> galaxies) {
        super(person.getPersonId(), person.getFirstName(), person.getLastName(), person.getPatronymic(), person.getRating());
        this.galaxies = galaxies;
    }
}
