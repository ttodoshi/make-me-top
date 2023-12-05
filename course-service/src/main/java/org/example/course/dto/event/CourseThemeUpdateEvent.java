package org.example.course.dto.event;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CourseThemeUpdateEvent {
    @NotBlank
    private String title;
    @NotNull
    private Integer courseThemeNumber;
    @NotNull
    private Long courseId;
}
