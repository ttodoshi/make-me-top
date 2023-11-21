package org.example.person.dto.course;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CourseWithRatingDto {
    private Integer courseId;
    private String title;
    private Double rating;
}
