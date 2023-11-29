package org.example.progress.dto.progress;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CourseWithThemesProgressDto {
    private Long courseId;
    private String title;
    @JsonProperty("planets")
    private List<CourseThemeCompletedDto> themesWithProgress;
}
