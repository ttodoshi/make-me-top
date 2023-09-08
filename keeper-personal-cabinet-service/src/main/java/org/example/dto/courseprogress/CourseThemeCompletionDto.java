package org.example.dto.courseprogress;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CourseThemeCompletionDto {
    private Integer courseThemeId;
    private Integer explorerId;
    private Integer mark;
}
