package org.example.person.dto.person;

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
    private Long galaxyId;
    private String galaxyName;

    public PersonWithGalaxyAndSystemsDto(Long personId, String firstName, String lastName, String patronymic, Double rating, Long galaxyId, String galaxyName, List<Long> systems) {
        super(personId, firstName, lastName, patronymic, rating, systems);
        this.galaxyId = galaxyId;
        this.galaxyName = galaxyName;
    }
}
