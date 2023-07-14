package org.example.dto.course;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
@NotNull
public class CourseUpdateRequest {
    @NotBlank
    private String title;
    private String description;
}
