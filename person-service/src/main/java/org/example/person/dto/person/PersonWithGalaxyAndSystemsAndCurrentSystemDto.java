package org.example.person.dto.person;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
public class PersonWithGalaxyAndSystemsAndCurrentSystemDto extends PersonWithGalaxyAndSystemsDto {
    private Long currentCourseId;
    private String currentCourseName;

    public PersonWithGalaxyAndSystemsAndCurrentSystemDto(PersonWithGalaxyAndSystemsDto personWithGalaxyAndSystemsDto, Long currentCourseId, String currentCourseName) {
        super(personWithGalaxyAndSystemsDto.getPersonId(), personWithGalaxyAndSystemsDto.getFirstName(), personWithGalaxyAndSystemsDto.getLastName(), personWithGalaxyAndSystemsDto.getPatronymic(), personWithGalaxyAndSystemsDto.getRating(), personWithGalaxyAndSystemsDto.getGalaxyId(), personWithGalaxyAndSystemsDto.getGalaxyName(), personWithGalaxyAndSystemsDto.getSystems());
        this.currentCourseId = currentCourseId;
        this.currentCourseName = currentCourseName;
    }
}
