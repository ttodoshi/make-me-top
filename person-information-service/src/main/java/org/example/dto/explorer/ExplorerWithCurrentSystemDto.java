package org.example.dto.explorer;

import lombok.*;
import org.example.dto.person.PersonWithGalaxyAndSystemsDto;

@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
public class ExplorerWithCurrentSystemDto extends PersonWithGalaxyAndSystemsDto {
    private Integer currentCourseId;
    private String currentCourseName;

    public ExplorerWithCurrentSystemDto(PersonWithGalaxyAndSystemsDto personWithGalaxyAndSystemsDto, Integer currentCourseId, String currentCourseName) {
        super(personWithGalaxyAndSystemsDto.getPersonId(), personWithGalaxyAndSystemsDto.getFirstName(), personWithGalaxyAndSystemsDto.getLastName(), personWithGalaxyAndSystemsDto.getPatronymic(), personWithGalaxyAndSystemsDto.getRating(), personWithGalaxyAndSystemsDto.getGalaxyId(), personWithGalaxyAndSystemsDto.getGalaxyName(), personWithGalaxyAndSystemsDto.getSystems());
        this.currentCourseId = currentCourseId;
        this.currentCourseName = currentCourseName;
    }
}
