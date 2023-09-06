package org.example.model.homework;

import lombok.Data;

import javax.persistence.*;

@Entity
@Table(name = "homework_feedback", schema = "course")
@Data
public class HomeworkFeedback {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer feedbackId;
    @JoinColumn(table = "homework_request", name = "request_id")
    private Integer requestId;
    private String comment;
    @JoinColumn(table = "homework_feedback_status", name = "status_id")
    private Integer statusId;
}
