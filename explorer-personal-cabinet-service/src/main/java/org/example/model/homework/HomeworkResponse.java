package org.example.model.homework;

import lombok.Data;

import javax.persistence.*;

@Entity
@Table(name = "homework_response", schema = "course")
@Data
public class HomeworkResponse {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer responseId;
    @JoinColumn(table = "homework_request", name = "request_id")
    private Integer requestId;
    private String content;
}
