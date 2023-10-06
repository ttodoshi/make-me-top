package org.example.dto.progress;

import lombok.Data;

@Data
public class CourseThemeCompletedDto {
    private Integer courseThemeId;
    private String title;
    private Boolean completed;
}
