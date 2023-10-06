package org.example.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.persistence.*;
import java.util.List;

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
    @OneToMany(mappedBy = "request", cascade = CascadeType.ALL)
    @JsonBackReference
    @ToString.Exclude
    private List<HomeworkFeedback> homeworkFeedbacks;

    public Homework(Integer courseThemeId, String content, Integer groupId) {
        this.courseThemeId = courseThemeId;
        this.content = content;
        this.groupId = groupId;
    }
}
