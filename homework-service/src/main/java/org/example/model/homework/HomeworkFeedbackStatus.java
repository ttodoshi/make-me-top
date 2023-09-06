package org.example.model.homework;

import lombok.Data;

import javax.persistence.*;

@Entity
@Table(name = "homework_feedback_status", schema = "course")
@Data
public class HomeworkFeedbackStatus {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer statusId;
    @Enumerated(EnumType.STRING)
    private HomeworkFeedbackStatusType status;
}
