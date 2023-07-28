package org.example.model.progress;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Data
@Entity
@Table(name = "course_theme_completion", schema = "course")
@AllArgsConstructor
@NoArgsConstructor
public class CourseThemeCompletion {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer courseThemeCompletionId;
    @JoinColumn(table = "explorer", name = "explorer_id")
    private Integer explorerId;
    private Integer courseThemeId;
    private Boolean completed;
    private Integer mark;
}
