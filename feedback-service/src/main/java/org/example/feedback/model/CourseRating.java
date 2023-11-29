package org.example.feedback.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "course_rating")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CourseRating {
    @Id
    private Long explorerId;
    @Column(nullable = false)
    private Integer rating;
}
