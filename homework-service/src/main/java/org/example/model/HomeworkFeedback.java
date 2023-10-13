package org.example.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Table(name = "homework_feedback")
@Data
@NoArgsConstructor
public class HomeworkFeedback {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer feedbackId;
    @ManyToOne(optional = false)
    @JoinColumn(name = "request_id", nullable = false, insertable = false, updatable = false)
    @JsonBackReference
    private HomeworkRequest request;
    @Column(name = "request_id")
    private Integer requestId;
    @Column(columnDefinition = "TEXT", nullable = false)
    private String comment;
    @ManyToOne(optional = false)
    @JoinColumn(name = "status_id", nullable = false, insertable = false, updatable = false)
    @JsonBackReference
    private HomeworkFeedbackStatus status;
    @Column(name = "status_id")
    private Integer statusId;

    public HomeworkFeedback(Integer requestId, String comment, Integer statusId) {
        this.requestId = requestId;
        this.comment = comment;
        this.statusId = statusId;
    }
}
