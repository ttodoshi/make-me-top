package org.example.model.feedback;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Table;

@Entity
@Table(name = "course_rating", schema = "course")
@Data
public class CourseRating {
    @Id
    @JoinColumn(table = "explorer", name = "explorer_id")
    private Integer explorerId;
    private Integer rating;
}
