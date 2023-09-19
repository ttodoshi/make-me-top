package org.example.model;

import lombok.Data;

import javax.persistence.*;

@Entity
@Table(name = "homework_request_status")
@Data
public class HomeworkRequestStatus {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer statusId;
    @Enumerated(EnumType.STRING)
    private HomeworkRequestStatusType status;
}
