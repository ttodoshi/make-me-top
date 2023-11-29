package org.example.person.dto.progress;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
public class CourseWithThemesProgressDto {
    private Long courseId;
    private String title;
    @JsonProperty("planets")
    private List<CourseThemeCompletedDto> themesWithProgress;
}
