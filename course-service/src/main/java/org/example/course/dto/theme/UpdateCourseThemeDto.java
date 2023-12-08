package org.example.course.dto.theme;

import lombok.Data;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Data
public class UpdateCourseThemeDto {
    @NotNull
    @Size(max = 255)
    private String title;
    @NotNull
    @Size(max = 255)
    private String description;
    @NotNull
    private String content;
    @NotNull
    @Min(value = 1)
    private Integer courseThemeNumber;
    @NotNull
    private Long courseId;
}
