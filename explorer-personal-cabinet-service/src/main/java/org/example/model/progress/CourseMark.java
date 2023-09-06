package org.example.model.progress;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "course_mark", schema = "course")
@Data
@EntityListeners(AuditingEntityListener.class)
public class CourseMark {
    @Id
    @JoinColumn(table = "explorer", name = "explorer_id")
    private Integer explorerId;
    @CreatedDate
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy hh:mm:ss")
    private LocalDateTime courseEndDate;
    private Integer value;
}
