package org.example.dto.courserequest;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.dto.keeper.KeeperDTO;

@Data
@NoArgsConstructor
public class CourseRegistrationRequestForExplorer {
    private Integer requestId;
    private Integer courseId;
    private String courseTitle;
    private Integer galaxyId;
    private String galaxyName;
    private KeeperDTO keeper;

    public CourseRegistrationRequestForExplorer(Integer requestId, Integer courseId, String courseTitle) {
        this.requestId = requestId;
        this.courseId = courseId;
        this.courseTitle = courseTitle;
    }
}
