package org.example.dto.theme;

import lombok.Data;

@Data
public class CourseThemeCreateRequest {
    private Integer courseThemeId;
    private String title;
    private String description;
    private String content;
    private Integer courseThemeNumber;
}
