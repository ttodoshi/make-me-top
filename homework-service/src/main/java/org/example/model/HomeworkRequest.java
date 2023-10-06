package org.example.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "homework_request")
@Data
@NoArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class HomeworkRequest {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer requestId;
    @JoinColumn(table = "homework", name = "homework_id")
    private Integer homeworkId;
    @Column(columnDefinition = "TEXT")
    private String content;
    private Integer explorerId;
    @CreatedDate
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy HH:mm:ss")
    private LocalDateTime requestDate;
    @JoinColumn(table = "homework_request_status", name = "status_id")
    private Integer statusId;

    public HomeworkRequest(Integer homeworkId, String content, Integer explorerId, Integer statusId) {
        this.homeworkId = homeworkId;
        this.content = content;
        this.explorerId = explorerId;
        this.statusId = statusId;
    }
}
