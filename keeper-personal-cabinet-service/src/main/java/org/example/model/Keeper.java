package org.example.model;

import lombok.Data;
import org.springframework.data.annotation.CreatedDate;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "keeper", schema = "course")
@Data
public class Keeper {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer keeperId;
    @JoinColumn(table = "course", name = "course_id")
    private Integer courseId;
    @JoinColumn(table = "person", name = "person_id")
    private Integer personId;
    @CreatedDate
    private Date startDate;
}
