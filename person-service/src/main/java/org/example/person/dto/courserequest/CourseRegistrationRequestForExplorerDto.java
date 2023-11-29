package org.example.person.dto.courserequest;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.person.dto.keeper.KeeperBasicInfoDto;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CourseRegistrationRequestForExplorerDto {
    private Long requestId;
    private Long courseId;
    private String courseTitle;
    private Long galaxyId;
    private String galaxyName;
    private List<KeeperBasicInfoDto> keepers;
}
