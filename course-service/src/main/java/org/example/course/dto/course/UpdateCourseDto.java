package org.example.course.dto.course;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Data
public class UpdateCourseDto {
    @NotBlank
    @Size(max = 255)
    private String title;
    @NotNull
    @Size(max = 255)
    private String description;
}
