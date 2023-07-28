package org.example.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "keeper", schema = "course")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class Keeper {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer keeperId;
    @JoinColumn(table = "course", name = "course_id")
    private Integer courseId;
    @JoinColumn(table = "person", name = "person_id")
    private Integer personId;
    @CreatedDate
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy hh:mm:ss")
    private Date startDate;
}
