package org.example.course.dto.theme;

import lombok.Data;

@Data
public class GetCourseThemeDto {
    private Integer courseThemeId;
    private String title;
    private String description;
    private Integer courseThemeNumber;
}
