package org.example.feedback.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import lombok.Data;

import javax.persistence.*;

@Entity
@Table(name = "course_rating")
@Data
public class CourseRating {
    @Id
    @Column(name = "explorer_id")
    private Long explorerId;
    @OneToOne(optional = false)
    @JoinColumn(name = "explorer_id", nullable = false, insertable = false, updatable = false)
    @JsonBackReference
    private CourseRatingOffer courseRatingOffer;
    @Column(nullable = false)
    private Integer rating;
}
