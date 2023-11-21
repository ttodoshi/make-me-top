package org.example.course.dto.event;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CourseCreateEvent {
    private Integer courseId;
    private String title;
    private String description;
}
