package org.example.person.dto.progress;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
public class CourseWithThemesProgressDto {
    private Integer courseId;
    private String title;
    @JsonProperty("planets")
    private List<CourseThemeCompletedDto> themesWithProgress;
}
