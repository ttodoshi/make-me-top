package org.example.model.homework;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Table;

@Entity
@Table(name = "homework_mark", schema = "course")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class HomeworkMark {
    @Id
    @JoinColumn(table = "homework_request", name = "request_id")
    private Integer requestId;
    private Integer mark;
    private String comment;
}
