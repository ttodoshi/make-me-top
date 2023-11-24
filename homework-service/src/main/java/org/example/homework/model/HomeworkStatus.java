package org.example.homework.model;

import lombok.Data;

import javax.persistence.*;

@Entity
@Table(name = "homework_status")
@Data
public class HomeworkStatus {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer statusId;
    @Column(nullable = false, unique = true)
    @Enumerated(EnumType.STRING)
    private HomeworkStatusType status;
}
