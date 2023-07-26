package org.example.dto.theme;

import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class CourseThemeUpdateRequest {
    @NotNull
    private String title;
    @NotNull
    private String description;
    @NotNull
    private String content;
    @NotNull
    private Integer courseThemeNumber;
    @NotNull
    private Integer courseId;
}
