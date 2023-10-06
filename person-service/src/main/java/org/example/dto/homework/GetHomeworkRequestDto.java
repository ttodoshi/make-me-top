package org.example.dto.homework;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.example.dto.explorer.ExplorerRequestDto;

@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
public class GetHomeworkRequestDto extends ExplorerRequestDto {
    private Integer requestId;
    private Integer explorerId;
    private Integer courseThemeId;
    private String courseThemeTitle;
    private Integer homeworkId;

    public GetHomeworkRequestDto(Integer requestId, Integer personId, String firstName, String lastName, String patronymic, Integer courseId, String courseTitle, Integer explorerId, Integer courseThemeId, String courseThemeTitle, Integer homeworkId) {
        super(personId, firstName, lastName, patronymic, courseId, courseTitle);
        this.requestId = requestId;
        this.explorerId = explorerId;
        this.courseThemeId = courseThemeId;
        this.courseThemeTitle = courseThemeTitle;
        this.homeworkId = homeworkId;
    }
}
