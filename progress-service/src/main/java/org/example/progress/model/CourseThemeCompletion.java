package org.example.progress.model;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Data
@Entity
@Table(name = "course_theme_completion")
@NoArgsConstructor
public class CourseThemeCompletion {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long courseThemeCompletionId;
    @Column(nullable = false)
    private Long explorerId;
    @Column(nullable = false)
    private Long courseThemeId;
    @Column(nullable = false)
    private Integer mark;

    public CourseThemeCompletion(Long explorerId, Long courseThemeId, Integer mark) {
        this.explorerId = explorerId;
        this.courseThemeId = courseThemeId;
        this.mark = mark;
    }
}
