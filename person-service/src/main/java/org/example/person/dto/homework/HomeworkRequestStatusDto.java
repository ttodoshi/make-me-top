package org.example.person.dto.homework;

import lombok.Data;

import javax.persistence.*;

@Entity
@Table(name = "homework_request_status")
@Data
public class HomeworkRequestStatusDto {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer statusId;
    @Column(nullable = false, unique = true)
    @Enumerated(EnumType.STRING)
    private HomeworkRequestStatusType status;
}
