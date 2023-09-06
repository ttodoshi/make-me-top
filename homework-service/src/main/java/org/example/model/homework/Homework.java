package org.example.model.homework;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Table(name = "homework", schema = "course")
@Data
@NoArgsConstructor
public class Homework {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer homeworkId;
    @JoinColumn(table = "course_theme", name = "course_theme_id")
    private Integer courseThemeId;
    private String content;
    @JoinColumn(table = "explorer_group", name = "group_id")
    private Integer groupId;

    public Homework(Integer courseThemeId, String content, Integer groupId) {
        this.courseThemeId = courseThemeId;
        this.content = content;
        this.groupId = groupId;
    }
}
