package org.example.event;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
public class CourseThemeCreateEvent {
    @NotNull
    private Integer courseThemeId;
    @NotBlank
    private String title;
    @NotNull
    private String description;
    @NotNull
    private String content;
    @NotNull
    private Integer courseThemeNumber;
}
