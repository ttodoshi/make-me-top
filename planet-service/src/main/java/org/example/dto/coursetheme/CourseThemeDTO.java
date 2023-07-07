package org.example.dto.coursetheme;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CourseThemeDTO {
    private Integer courseThemeId;
    private String title;
    private Date lastModified;
    private String description;
    private String content;
    private Integer courseThemeNumber;
    private Integer courseId;
}
