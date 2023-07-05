package org.example.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Table;
import java.util.Date;

@Entity
@Table(name = "homework_request", schema = "course")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class HomeworkRequest {
    @Id
    private Integer requestId;
    @JoinColumn(table = "homework", name = "homework_id")
    private Integer homeworkId;
    private String content;
    @JoinColumn(table = "keeper", name = "keeper_id")
    private Integer keeperId;
    @JoinColumn(table = "explorer", name = "explorer_id")
    private Integer explorerId;
    @CreatedDate
    private Date requestDate;
    @JoinColumn(table = "homework_request_status", name = "status_id")
    private Integer statusId;
}
