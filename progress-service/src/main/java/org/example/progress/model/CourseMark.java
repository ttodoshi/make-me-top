package org.example.progress.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "course_mark")
@Data
@NoArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class CourseMark {
    @Id
    private Long explorerId;
    @CreatedDate
    @Column(nullable = false)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy HH:mm:ss")
    private LocalDateTime courseEndDate;
    @Column(nullable = false)
    private Integer value;

    public CourseMark(Long explorerId, Integer value) {
        this.explorerId = explorerId;
        this.value = value;
    }
}
