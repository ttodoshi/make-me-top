package org.example.person.dto.homework;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.example.person.dto.explorer.ExplorerRequestDto;

@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
public class GetHomeworkRequestDto extends ExplorerRequestDto {
    private Long requestId;
    private Long explorerId;
    private Long courseThemeId;
    private String courseThemeTitle;
    private Long homeworkId;
    private HomeworkRequestStatusDto status;

    public GetHomeworkRequestDto(Long requestId, Long personId, String firstName, String lastName, String patronymic, Long courseId, String courseTitle, Long explorerId, Long courseThemeId, String courseThemeTitle, Long homeworkId, HomeworkRequestStatusDto status) {
        super(personId, firstName, lastName, patronymic, courseId, courseTitle);
        this.requestId = requestId;
        this.explorerId = explorerId;
        this.courseThemeId = courseThemeId;
        this.courseThemeTitle = courseThemeTitle;
        this.homeworkId = homeworkId;
        this.status = status;
    }
}
