package org.example.homework.model;

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
@Table(name = "homework_request_version")
@Data
@NoArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class HomeworkRequestVersion {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long versionId;
    @ManyToOne(optional = false)
    @JoinColumn(name = "request_id", nullable = false, insertable = false, updatable = false)
    @JsonBackReference
    private HomeworkRequest request;
    @Column(name = "request_id")
    private Long requestId;
    @Column(columnDefinition = "TEXT", nullable = false)
    private String content;
    @CreatedDate
    @Column(nullable = false)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy HH:mm:ss")
    private LocalDateTime creationDate;
    @OneToMany(mappedBy = "requestVersion", cascade = CascadeType.ALL)
    @JsonBackReference
    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    private List<HomeworkRequestFeedback> homeworkRequestFeedbacks;

    public HomeworkRequestVersion(Long requestId, String content) {
        this.requestId = requestId;
        this.content = content;
    }
}
