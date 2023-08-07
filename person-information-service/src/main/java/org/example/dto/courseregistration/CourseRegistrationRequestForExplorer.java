package org.example.dto.courseregistration;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class CourseRegistrationRequestForExplorer {
    private Integer requestId;
    private Integer courseId;
    private String courseTitle;
    private Integer galaxyId;
    private String galaxyName;
    private Integer keeperPersonId;
    private String keeperFirstName;
    private String keeperLastName;
    private String keeperPatronymic;
    private Integer keeperId;

    public CourseRegistrationRequestForExplorer(Integer requestId, Integer courseId, String courseTitle, Integer keeperPersonId, String keeperFirstName, String keeperLastName, String keeperPatronymic, Integer keeperId) {
        this.requestId = requestId;
        this.courseId = courseId;
        this.courseTitle = courseTitle;
        this.keeperPersonId = keeperPersonId;
        this.keeperFirstName = keeperFirstName;
        this.keeperLastName = keeperLastName;
        this.keeperPatronymic = keeperPatronymic;
        this.keeperId = keeperId;
    }
}
