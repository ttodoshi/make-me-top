package org.example.dto.person;

import lombok.*;
import org.example.dto.explorer.PersonWithSystemsDto;

import java.util.List;

@Data
@EqualsAndHashCode(callSuper = true)
@With
@AllArgsConstructor
@NoArgsConstructor
public class PersonWithGalaxyAndSystemsDto extends PersonWithSystemsDto {
    private Integer galaxyId;
    private String galaxyName;

    public PersonWithGalaxyAndSystemsDto(Integer personId, String firstName, String lastName, String patronymic, Double rating, Integer galaxyId, String galaxyName, List<Integer> systems) {
        super(personId, firstName, lastName, patronymic, rating, systems);
        this.galaxyId = galaxyId;
        this.galaxyName = galaxyName;
    }
}
