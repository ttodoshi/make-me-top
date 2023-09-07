package org.example.dto.courseprogress;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.dto.keeper.KeeperDTO;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CurrentCourseProgressDTO {
    private Integer explorerId;
    private Integer groupId;
    private Integer courseThemeId;
    private String courseThemeTitle;
    private Integer courseId;
    private String courseTitle;
    private KeeperDTO keeper;
    private Double progress;
}
