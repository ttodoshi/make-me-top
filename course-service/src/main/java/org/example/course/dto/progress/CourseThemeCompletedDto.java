package org.example.course.dto.progress;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CourseThemeCompletedDto {
    private Long courseThemeId;
    private String title;
    private Boolean completed;
}
