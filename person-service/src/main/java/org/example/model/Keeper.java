package org.example.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "keeper")
@Data
@NoArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class Keeper {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer keeperId;
    @Column(nullable = false)
    private Integer courseId;
    @ManyToOne(optional = false)
    @JoinColumn(name = "person_id", nullable = false, insertable = false, updatable = false)
    @JsonBackReference
    private Person person;
    @Column(name = "person_id")
    private Integer personId;
    @CreatedDate
    @Column(nullable = false)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy HH:mm:ss")
    private LocalDateTime startDate;

    public Keeper(Integer courseId, Integer personId) {
        this.courseId = courseId;
        this.personId = personId;
    }
}
