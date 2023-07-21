package org.example.dto.theme;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
@NotNull
public class CourseThemeCreateRequest {
    private Integer courseThemeId;
    @NotBlank
    private String title;
    private String description;
    private String content;
    private Integer courseThemeNumber;
}
