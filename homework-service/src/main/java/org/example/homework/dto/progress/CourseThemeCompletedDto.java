package org.example.homework.dto.progress;

import lombok.Data;

@Data
public class CourseThemeCompletedDto {
    private Integer courseThemeId;
    private String title;
    private Integer courseThemeNumber;
    private Boolean completed;
}
