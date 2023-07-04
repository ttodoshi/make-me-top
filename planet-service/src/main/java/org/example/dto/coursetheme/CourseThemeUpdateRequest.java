package org.example.dto.coursetheme;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CourseThemeUpdateRequest {
    private String title;
    private String description;
    private String content;
    private Integer courseId;
}
