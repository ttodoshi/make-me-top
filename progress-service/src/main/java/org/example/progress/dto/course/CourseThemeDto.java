package org.example.progress.dto.course;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class CourseThemeDto {
    private Integer courseThemeId;
    private String title;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy HH:mm:ss")
    private LocalDateTime lastModified;
    private String description;
    private String content;
    private Integer courseThemeNumber;
    private Integer courseId;
}
