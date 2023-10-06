package org.example.dto.courserequest;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.dto.keeper.KeeperBasicInfoDto;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CourseRegistrationRequestForExplorerDto {
    private Integer requestId;
    private Integer courseId;
    private String courseTitle;
    private Integer galaxyId;
    private String galaxyName;
    private List<KeeperBasicInfoDto> keeper;
}
