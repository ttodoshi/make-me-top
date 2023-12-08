package org.example.homework.model;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Table(name = "homework")
@Data
@NoArgsConstructor
public class Homework {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long homeworkId;
    @Column(nullable = false)
    private Long courseThemeId;
    @Column(columnDefinition = "TEXT", nullable = false)
    private String content;
    @Column(nullable = false)
    private Long groupId;

    public Homework(Long courseThemeId, String content, Long groupId) {
        this.courseThemeId = courseThemeId;
        this.content = content;
        this.groupId = groupId;
    }
}
