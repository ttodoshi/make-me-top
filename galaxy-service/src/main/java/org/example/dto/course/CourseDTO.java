package org.example.dto.course;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CourseDTO {
    private Integer courseId;
    private String title;
    private Date creationDate;
    private Date lastModified;
    private String description;
}
