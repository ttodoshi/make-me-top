package org.example.person.dto.person;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.example.person.dto.galaxy.GalaxyDto;

import java.util.List;

@Data
@EqualsAndHashCode(callSuper = true)
public class PersonWithGalaxiesDto extends PersonWithRatingDto {
    private List<GalaxyDto> galaxies;

    public PersonWithGalaxiesDto(Long personId, String firstName, String lastName, String patronymic, Double rating, List<GalaxyDto> galaxies) {
        super(personId, firstName, lastName, patronymic, rating);
        this.galaxies = galaxies;
    }
}
