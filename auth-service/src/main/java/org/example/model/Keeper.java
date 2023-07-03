package org.example.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "keeper", schema = "course")
@Data
public class Keeper {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer keeperId;
    @JoinTable(name = "course")
    @JoinColumn(name = "course_id")
    private Integer courseId;
    @JoinTable(name = "person")
    @JoinColumn(name = "person_id")
    private Integer personId;
    private Date startDate;
}
