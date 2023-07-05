package org.example.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Data
@Entity
@Table(name = "course_progress", schema = "course")
@AllArgsConstructor
@NoArgsConstructor
public class CourseProgress {
    @Id
    @JoinColumn(table = "explorer", name = "explorer_id")
    private Integer explorerId;
    private Integer progress;
}
