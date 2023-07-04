package org.example.model;

import lombok.Data;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "course_theme", schema = "course")
@Data
public class CourseTheme {
    @Id
    private Integer courseThemeId;
    private String title;
    private Date lastModified;
    private String description;
    private String content;
    @JoinTable(name = "course")
    @JoinColumn(name = "course_id")
    private Integer courseId;
}
