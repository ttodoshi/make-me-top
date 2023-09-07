package org.example.dto.theme;

import lombok.Data;

@Data
public class CourseThemeGetResponse {
    private Integer courseThemeId;
    private String title;
    private String description;
    private Integer courseThemeNumber;
}
