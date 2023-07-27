package org.example.model.course;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "course_theme", schema = "course")
@Data
@EntityListeners(AuditingEntityListener.class)
public class CourseTheme {
    @Id
    private Integer courseThemeId;
    private String title;
    @LastModifiedDate
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy hh:mm:ss")
    private Date lastModified;
    private String description;
    private String content;
    @JoinColumn(table = "course", name = "course_id")
    private Integer courseThemeNumber;
    private Integer courseId;
}