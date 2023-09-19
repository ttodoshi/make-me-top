package org.example.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Table(name = "homework_mark")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class HomeworkMark {
    @Id
    @JoinColumn(table = "homework_request", name = "request_id")
    private Integer requestId;
    private Integer mark;
    @Column(columnDefinition = "TEXT")
    private String comment;
}
