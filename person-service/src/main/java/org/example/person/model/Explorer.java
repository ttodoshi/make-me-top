package org.example.person.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "explorer")
@Data
@AllArgsConstructor
@NoArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class Explorer {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long explorerId;
    @ManyToOne(optional = false)
    @JoinColumn(name = "person_id", nullable = false, insertable = false, updatable = false)
    @JsonBackReference
    private Person person;
    @Column(name = "person_id")
    private Long personId;
    @ManyToOne(optional = false)
    @JoinColumn(name = "group_id", nullable = false, insertable = false, updatable = false)
    @JsonBackReference
    private ExplorerGroup group;
    @Column(name = "group_id")
    private Long groupId;
    @CreatedDate
    @Column(nullable = false)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy HH:mm:ss")
    private LocalDateTime startDate;

    public Explorer(Long personId, Long groupId) {
        this.personId = personId;
        this.groupId = groupId;
    }
}
