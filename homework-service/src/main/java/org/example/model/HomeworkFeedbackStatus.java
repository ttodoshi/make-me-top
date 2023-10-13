package org.example.model;

import lombok.Data;

import javax.persistence.*;

@Entity
@Table(name = "homework_feedback_status")
@Data
public class HomeworkFeedbackStatus {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer statusId;
    @Column(nullable = false, unique = true)
    @Enumerated(EnumType.STRING)
    private HomeworkFeedbackStatusType status;
}
