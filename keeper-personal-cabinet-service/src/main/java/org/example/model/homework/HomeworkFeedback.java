package org.example.model.homework;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Table(name = "homework_feedback", schema = "course")
@Data
@NoArgsConstructor
public class HomeworkFeedback {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer feedbackId;
    @JoinColumn(table = "homework_request", name = "request_id")
    private Integer requestId;
    private String comment;
    @JoinColumn(table = "homework_feedback_status", name = "status_id")
    private Integer statusId;

    public HomeworkFeedback(Integer requestId, String comment, Integer statusId) {
        this.requestId = requestId;
        this.comment = comment;
        this.statusId = statusId;
    }
}
