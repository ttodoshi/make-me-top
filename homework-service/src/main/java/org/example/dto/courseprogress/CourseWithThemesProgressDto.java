package org.example.dto.courseprogress;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CourseWithThemesProgressDto {
    private Integer courseId;
    private String title;
    @JsonProperty("planets")
    private List<CourseThemeCompletedDto> themesWithProgress;
}
