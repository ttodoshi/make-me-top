package org.example.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "homework_request")
@Data
@NoArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class HomeworkRequest {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer requestId;
    @ManyToOne(optional = false)
    @JoinColumn(name = "homework_id", nullable = false, insertable = false, updatable = false)
    @JsonBackReference
    private Homework homework;
    @Column(name = "homework_id")
    private Integer homeworkId;
    @Column(columnDefinition = "TEXT", nullable = false)
    private String content;
    @Column(nullable = false)
    private Integer explorerId;
    @CreatedDate
    @Column(nullable = false)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy HH:mm:ss")
    private LocalDateTime requestDate;
    @ManyToOne(optional = false)
    @JoinColumn(name = "status_id", nullable = false, insertable = false, updatable = false)
    @JsonBackReference
    private HomeworkRequestStatus status;
    @Column(name = "status_id")
    private Integer statusId;
    @OneToMany(mappedBy = "request", cascade = CascadeType.ALL)
    @JsonBackReference
    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    private List<HomeworkMark> homeworkMarks;

    public HomeworkRequest(Integer homeworkId, String content, Integer explorerId, Integer statusId) {
        this.homeworkId = homeworkId;
        this.content = content;
        this.explorerId = explorerId;
        this.statusId = statusId;
    }
}
