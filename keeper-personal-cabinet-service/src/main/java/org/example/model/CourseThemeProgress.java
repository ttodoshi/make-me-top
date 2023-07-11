package org.example.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Data
@Entity
@Table(name = "course_theme_progress", schema = "course")
@AllArgsConstructor
@NoArgsConstructor
public class CourseThemeProgress {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer courseThemeProgressId;
    @JoinColumn(table = "explorer", name = "explorer_id")
    private Integer explorerId;
    private Integer courseThemeId;
    private Integer progress;
}
