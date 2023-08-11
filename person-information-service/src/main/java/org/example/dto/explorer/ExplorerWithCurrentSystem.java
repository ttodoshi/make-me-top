package org.example.dto.explorer;

import lombok.*;
import org.example.dto.person.PersonWithRatingAndGalaxyDTO;

import java.util.List;

@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
public class ExplorerWithCurrentSystem extends PersonWithRatingAndGalaxyDTO {
    private Integer currentCourseId;
    private String currentCourseName;

    public ExplorerWithCurrentSystem(PersonWithRatingAndGalaxyDTO personWithRatingAndGalaxyDTO, Integer currentCourseId, String currentCourseName) {
        super(personWithRatingAndGalaxyDTO.getPersonId(), personWithRatingAndGalaxyDTO.getFirstName(), personWithRatingAndGalaxyDTO.getLastName(), personWithRatingAndGalaxyDTO.getPatronymic(), personWithRatingAndGalaxyDTO.getRating(), personWithRatingAndGalaxyDTO.getGalaxyId(), personWithRatingAndGalaxyDTO.getGalaxyName(), personWithRatingAndGalaxyDTO.getSystems());
        this.currentCourseId = currentCourseId;
        this.currentCourseName = currentCourseName;
    }
}
