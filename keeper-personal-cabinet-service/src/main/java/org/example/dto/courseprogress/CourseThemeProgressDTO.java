package org.example.dto.courseprogress;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CourseThemeProgressDTO {
    private Integer explorerId;
    private Integer courseThemeId;
    private Integer mark;
}
