package org.example.model;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Table;
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
    @JoinColumn(table = "course", name = "course_id")
    private Integer courseId;
}
