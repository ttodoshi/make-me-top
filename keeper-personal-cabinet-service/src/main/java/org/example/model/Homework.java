package org.example.model;

import lombok.Data;

import javax.persistence.*;

@Entity
@Table(name = "homework", schema = "course")
@Data
public class Homework {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer homeworkId;
    @JoinColumn(table = "course_theme", name = "course_theme_id")
    private Integer courseThemeId;
    private String content;
}
