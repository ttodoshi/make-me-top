package org.example.model;

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
    private Integer explorerId;
    @Column(nullable = false)
    private Integer rating;
}
