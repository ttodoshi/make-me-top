package org.example.dto.person;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@EqualsAndHashCode(callSuper = true)
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
