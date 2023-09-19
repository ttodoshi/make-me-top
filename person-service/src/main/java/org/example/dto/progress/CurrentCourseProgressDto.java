package org.example.dto.progress;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.dto.keeper.KeeperBasicInfoDto;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CurrentCourseProgressDto {
    private Integer explorerId;
    private Integer groupId;
    private Integer courseThemeId;
    private String courseThemeTitle;
    private Integer courseId;
    private String courseTitle;
    private KeeperBasicInfoDto keeper;
    private Double progress;
}
