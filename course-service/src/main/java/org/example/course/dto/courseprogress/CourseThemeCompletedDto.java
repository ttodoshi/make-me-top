package org.example.course.dto.courseprogress;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CourseThemeCompletedDto {
    private Integer courseThemeId;
    private String title;
    private Boolean completed;
}
