package org.example.person.dto.feedback.offer;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CourseRatingOfferProfileDto {
    private Long explorerId;
    private Long courseId;
    private String courseTitle;
}
