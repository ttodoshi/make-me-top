package org.example.person.dto.courserequest;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.example.person.dto.explorer.ExplorerRequestDto;

@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
public class CourseRegistrationRequestForKeeperWithGalaxyDto extends ExplorerRequestDto {
    private Long requestId;
    private Double rating;
    private Long galaxyId;
    private String galaxyName;

    public CourseRegistrationRequestForKeeperWithGalaxyDto(Long requestId, Long personId, String firstName, String lastName, String patronymic, Long courseId, String courseTitle, Double rating, Long galaxyId, String galaxyName) {
        super(personId, firstName, lastName, patronymic, courseId, courseTitle);
        this.requestId = requestId;
        this.rating = rating;
        this.galaxyId = galaxyId;
        this.galaxyName = galaxyName;
    }
}
