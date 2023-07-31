package org.example.dto.courseprogress;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Builder
@Data
public class CourseWithThemesProgress {
    private Integer courseId;
    private String title;
    @JsonProperty("planets")
    private List<CourseThemeCompletionDTO> themesWithProgress;
}
