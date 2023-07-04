package org.example.dto;

import lombok.Data;

@Data
public class CourseThemeCreateRequest {
    private Integer courseThemeId;
    private String title;
    private String description;
    private String content;
}
