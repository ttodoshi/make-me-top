package org.example.course.dto.theme;

import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class UpdateCourseThemeDto {
    @NotNull
    private String title;
    @NotNull
    private String description;
    @NotNull
    private String content;
    @NotNull
    private Integer courseThemeNumber;
    @NotNull
    private Long courseId;
}
