package org.example.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "explorer", schema = "course")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Explorer {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer explorerId;
    @JoinTable(name = "person")
    @JoinColumn(name = "person_id")
    private Integer personId;
    @JoinTable(name = "course")
    @JoinColumn(name = "course_id")
    private Integer courseId;
    private Date startDate;
}
