package org.example.dto.theme;

import lombok.Data;

@Data
public class CourseThemeUpdateRequest {
    private String title;
    private String description;
    private String content;
    private Integer courseThemeNumber;
    private Integer courseId;
}
