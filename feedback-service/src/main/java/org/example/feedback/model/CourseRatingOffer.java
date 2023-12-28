package org.example.feedback.model;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "course_rating_offer")
@Data
@NoArgsConstructor
public class CourseRatingOffer {
    @Id
    private Long explorerId;
    @Column(nullable = false)
    private Boolean offerValid;

    public CourseRatingOffer(Long explorerId) {
        this.explorerId = explorerId;
        this.offerValid = true;
    }
}
