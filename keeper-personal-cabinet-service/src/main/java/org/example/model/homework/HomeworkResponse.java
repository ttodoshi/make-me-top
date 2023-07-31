package org.example.model.homework;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Table(name = "homework_response", schema = "course")
@Data
@NoArgsConstructor
public class HomeworkResponse {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer responseId;
    @JoinColumn(table = "homework_request", name = "request_id")
    private Integer requestId;
    private String content;

    public HomeworkResponse(Integer requestId, String content) {
        this.requestId = requestId;
        this.content = content;
    }
}
