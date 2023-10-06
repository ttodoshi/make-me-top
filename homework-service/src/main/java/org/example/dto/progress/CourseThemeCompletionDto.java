package org.example.dto.progress;

import lombok.Data;

@Data
public class CourseThemeCompletionDto {
    private Integer courseThemeCompletionId;
    private Integer explorerId;
    private Integer courseThemeId;
    private Integer mark;
}
