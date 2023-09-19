package org.example.model;

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
    private Integer homeworkId;
    private Integer courseThemeId;
    @Column(columnDefinition = "TEXT")
    private String content;
    private Integer groupId;

    public Homework(Integer courseThemeId, String content, Integer groupId) {
        this.courseThemeId = courseThemeId;
        this.content = content;
        this.groupId = groupId;
    }
}
