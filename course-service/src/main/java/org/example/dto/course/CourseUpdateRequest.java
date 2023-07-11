package org.example.dto.course;

import lombok.Data;

@Data
public class CourseUpdateRequest {
    private String title;
    private String description;
}
