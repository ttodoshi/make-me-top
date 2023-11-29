package org.example.course.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "course_theme")
@Data
@AllArgsConstructor
@NoArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class CourseTheme {
    @Id
    @Column(nullable = false)
    private Long courseThemeId;
    @Column(nullable = false)
    private String title;
    @LastModifiedDate
    @Column(nullable = false)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy HH:mm:ss")
    private LocalDateTime lastModified;
    @Column(nullable = false)
    private String description;
    @Column(columnDefinition = "TEXT", nullable = false)
    private String content;
    @Column(nullable = false)
    private Integer courseThemeNumber;
    @ManyToOne(optional = false)
    @JoinColumn(name = "course_id", nullable = false, insertable = false, updatable = false)
    @JsonBackReference
    private Course course;
    @Column(name = "course_id")
    private Long courseId;
}
