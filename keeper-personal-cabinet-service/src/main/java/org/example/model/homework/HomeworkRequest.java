package org.example.model.homework;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "homework_request", schema = "course")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class HomeworkRequest {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer requestId;
    @JoinColumn(table = "homework", name = "homework_id")
    private Integer homeworkId;
    private String content;
    @JoinColumn(table = "keeper", name = "keeper_id")
    private Integer keeperId;
    @JoinColumn(table = "explorer", name = "explorer_id")
    private Integer explorerId;
    @CreatedDate
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy hh:mm:ss")
    private Date requestDate;
    @JoinColumn(table = "homework_request_status", name = "status_id")
    private Integer statusId;
}