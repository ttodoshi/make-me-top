package org.example.person.dto.progress;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.example.person.dto.keeper.KeeperBasicInfoDto;

@Data
@EqualsAndHashCode(callSuper = true)
public class CurrentCourseProgressProfileDto extends CurrentCourseProgressPublicDto {
    private Long groupId;
    private Double progress;
    private Long unfulfilledHomeworksNumber;

    public CurrentCourseProgressProfileDto(Long explorerId, Long courseThemeId, String courseThemeTitle, Long courseId, String courseTitle, KeeperBasicInfoDto keeper, Long groupId, Double progress, Long unfulfilledHomeworksNumber) {
        super(explorerId, courseThemeId, courseThemeTitle, courseId, courseTitle, keeper);
        this.groupId = groupId;
        this.progress = progress;
        this.unfulfilledHomeworksNumber = unfulfilledHomeworksNumber;
    }
}
