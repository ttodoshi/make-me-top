package org.example.model;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Table;

@Entity
@Table(name = "homework", schema = "course")
@Data
public class Homework {
    @Id
    private Integer homeworkId;
    @JoinColumn(table = "course_theme", name = "course_theme_id")
    private Integer courseThemeId;
    private String content;
}
