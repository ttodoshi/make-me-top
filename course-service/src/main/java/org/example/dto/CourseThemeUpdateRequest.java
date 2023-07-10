package org.example.dto;

import lombok.Data;

@Data
public class CourseThemeUpdateRequest {
    private String title;
    private String description;
    private String content;
    private Integer courseThemeNumber;
    private Integer courseId;
}
