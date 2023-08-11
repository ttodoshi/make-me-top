package org.example.dto.courseregistration;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
public class CourseRegistrationRequestForKeeperWithGalaxy extends CourseRegistrationRequestForKeeper {
    private Integer galaxyId;
    private String galaxyName;

    public CourseRegistrationRequestForKeeperWithGalaxy(Integer requestId, Integer personId, String firstName, String lastName, String patronymic, Integer courseId, String courseTitle, Integer keeperId) {
        super(requestId, personId, firstName, lastName, patronymic, courseId, courseTitle, keeperId);
    }
}
