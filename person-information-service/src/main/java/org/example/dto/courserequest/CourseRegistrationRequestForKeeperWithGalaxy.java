package org.example.dto.courserequest;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.example.dto.explorer.ExplorerRequest;

@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
public class CourseRegistrationRequestForKeeperWithGalaxy extends ExplorerRequest {
    private Integer requestId;
    private Double rating;
    private Integer galaxyId;
    private String galaxyName;

    public CourseRegistrationRequestForKeeperWithGalaxy(Integer requestId, Integer personId, String firstName, String lastName, String patronymic, Integer courseId, String courseTitle) {
        super(personId, firstName, lastName, patronymic, courseId, courseTitle);
        this.requestId = requestId;
    }
}
