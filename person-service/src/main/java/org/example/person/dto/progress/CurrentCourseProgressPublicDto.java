package org.example.person.dto.progress;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.person.dto.keeper.KeeperBasicInfoDto;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CurrentCourseProgressPublicDto {
    private Long explorerId;
    private Long courseThemeId;
    private String courseThemeTitle;
    private Long courseId;
    private String courseTitle;
    private KeeperBasicInfoDto keeper;
}
