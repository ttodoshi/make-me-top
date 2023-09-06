package org.example.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Table(name = "explorer_group", schema = "course")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ExplorerGroup {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer groupId;
    @JoinColumn(table = "course", name = "course_id")
    private Integer courseId;
    @JoinColumn(table = "keeper", name = "keeper_id")
    private Integer keeperId;
}
