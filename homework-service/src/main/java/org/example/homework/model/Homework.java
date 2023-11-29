package org.example.homework.model;

import com.fasterxml.jackson.annotation.JsonInclude;
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
    @ManyToOne(optional = false)
    @JoinColumn(name = "status_id", nullable = false, insertable = false, updatable = false)
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private HomeworkStatus status;
    @Column(name = "status_id")
    private Long statusId;

    public Homework(Long courseThemeId, String content, Long groupId, Long statusId) {
        this.courseThemeId = courseThemeId;
        this.content = content;
        this.groupId = groupId;
        this.statusId = statusId;
    }
}
