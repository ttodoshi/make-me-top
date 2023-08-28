package org.example.dto.courseprogress;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CourseWithThemesProgress {
    private Integer courseId;
    private String title;
    @JsonProperty("planets")
    private List<CourseThemeCompletionDTO> themesWithProgress;
}
