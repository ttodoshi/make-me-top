package org.example.dto.courserequest;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.dto.keeper.KeeperDto;

@Data
@NoArgsConstructor
public class CourseRegistrationRequestForExplorerDto {
    private Integer requestId;
    private Integer courseId;
    private String courseTitle;
    private Integer galaxyId;
    private String galaxyName;
    private KeeperDto keeper;

    public CourseRegistrationRequestForExplorerDto(Integer requestId, Integer courseId, String courseTitle) {
        this.requestId = requestId;
        this.courseId = courseId;
        this.courseTitle = courseTitle;
    }
}
