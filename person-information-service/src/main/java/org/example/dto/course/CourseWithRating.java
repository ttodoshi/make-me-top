package org.example.dto.course;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CourseWithRating {
    private Integer courseId;
    private String title;
    private Double rating;
    private Integer keeperId;
}
