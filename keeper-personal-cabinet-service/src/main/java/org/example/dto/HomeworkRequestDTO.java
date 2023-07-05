package org.example.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
public class HomeworkRequestDTO extends ExplorerRequest {
    private Integer explorerId;
    private Integer courseThemeId;
    private String courseThemeTitle;

    public HomeworkRequestDTO(Integer personId, String firstName, String lastName, String patronymic, Integer courseId, String courseTitle, Integer explorerId, Integer courseThemeId, String courseThemeTitle) {
        super(personId, firstName, lastName, patronymic, courseId, courseTitle);
        this.explorerId = explorerId;
        this.courseThemeId = courseThemeId;
        this.courseThemeTitle = courseThemeTitle;
    }
}
