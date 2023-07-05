package org.example.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.data.annotation.CreatedDate;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Table;
import java.util.Date;

@Entity
@Table(name = "course_mark", schema = "course")
@Data
public class CourseMark {
    @Id
    @JoinColumn(table = "explorer", name = "explorer_id")
    private Integer explorerId;
    @CreatedDate
    private Date endDate;
    private Integer value;
}
