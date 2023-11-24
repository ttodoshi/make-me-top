package org.example.homework.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "homework_request_feedback")
@Data
@NoArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class HomeworkRequestFeedback {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer feedbackId;
    @ManyToOne(optional = false)
    @JoinColumn(name = "request_version_id", nullable = false, insertable = false, updatable = false)
    @JsonBackReference
    private HomeworkRequestVersion requestVersion;
    @Column(name = "request_version_id")
    private Integer requestVersionId;
    @Column(columnDefinition = "TEXT", nullable = false)
    private String comment;
    @CreatedDate
    @Column(nullable = false)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy HH:mm:ss")
    private LocalDateTime creationDate;

    public HomeworkRequestFeedback(Integer requestVersionId, String comment) {
        this.requestVersionId = requestVersionId;
        this.comment = comment;
    }
}
