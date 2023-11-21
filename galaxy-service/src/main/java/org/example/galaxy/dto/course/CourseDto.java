package org.example.galaxy.dto.course;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class CourseDto {
    private Integer courseId;
    private String title;
}
