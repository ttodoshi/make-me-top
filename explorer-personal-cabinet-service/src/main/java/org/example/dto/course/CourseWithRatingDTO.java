package org.example.dto.course;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CourseWithRatingDTO {
    private Integer courseId;
    private String title;
    private Double rating;
}
