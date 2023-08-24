package org.example.model.course;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import org.springframework.data.annotation.LastModifiedDate;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Table;
import java.time.LocalDateTime;

@Entity
@Table(name = "course_theme", schema = "course")
@Data
public class CourseTheme {
    @Id
    private Integer courseThemeId;
    private String title;
    @LastModifiedDate
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy hh:mm:ss")
    private LocalDateTime lastModified;
    private String description;
    private String content;
    private Integer courseThemeNumber;
    @JoinColumn(table = "course", name = "course_id")
    private Integer courseId;
}
