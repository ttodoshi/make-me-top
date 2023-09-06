package org.example.model;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Table(name = "explorer_group", schema = "course")
@Data
@NoArgsConstructor
public class ExplorerGroup {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer groupId;
    @JoinColumn(table = "course", name = "course_id")
    private Integer courseId;
    @JoinColumn(table = "keeper", name = "keeper_id")
    private Integer keeperId;

    public ExplorerGroup(Integer courseId, Integer keeperId) {
        this.courseId = courseId;
        this.keeperId = keeperId;
    }
}
