package org.example.galaxy.dto.course;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class CourseDto {
    private Long courseId;
    private String title;
}
