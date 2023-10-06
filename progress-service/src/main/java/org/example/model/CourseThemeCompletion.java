package org.example.model;

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
    private Integer courseThemeCompletionId;
    private Integer explorerId;
    private Integer courseThemeId;
    private Integer mark;

    public CourseThemeCompletion(Integer explorerId, Integer courseThemeId, Integer mark) {
        this.explorerId = explorerId;
        this.courseThemeId = courseThemeId;
        this.mark = mark;
    }
}
