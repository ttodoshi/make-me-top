package org.example.dto.theme;

import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
@NotNull
public class CourseThemeUpdateRequest {
    private String title;
    private String description;
    private String content;
    private Integer courseThemeNumber;
    private Integer courseId;
}
