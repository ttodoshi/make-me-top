package org.example.courseregistration.model;

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
@Table(name = "course_registration_request")
@Data
@NoArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class CourseRegistrationRequest {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long requestId;
    @Column(nullable = false)
    private Long courseId;
    @Column(nullable = false)
    private Long personId;
    @CreatedDate
    @Column(nullable = false)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy HH:mm:ss")
    private LocalDateTime requestDate;
    @ManyToOne(optional = false)
    @JoinColumn(name = "status_id", nullable = false, insertable = false, updatable = false)
    @JsonBackReference
    private CourseRegistrationRequestStatus status;
    @Column(name = "status_id")
    private Long statusId;
    @OneToMany(mappedBy = "request", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonBackReference
    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    private List<CourseRegistrationRequestKeeper> keepersRequests;

    public CourseRegistrationRequest(Long courseId, Long personId, Long statusId) {
        this.courseId = courseId;
        this.personId = personId;
        this.statusId = statusId;
    }
}
