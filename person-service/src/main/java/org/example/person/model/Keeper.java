package org.example.person.model;

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
@Table(name = "keeper")
@Data
@NoArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class Keeper {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long keeperId;
    @Column(nullable = false)
    private Long courseId;
    @ManyToOne(optional = false)
    @JoinColumn(name = "person_id", nullable = false, insertable = false, updatable = false)
    @JsonBackReference
    private Person person;
    @Column(name = "person_id")
    private Long personId;
    @CreatedDate
    @Column(nullable = false)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy HH:mm:ss")
    private LocalDateTime startDate;
    @OneToMany(mappedBy = "keeper", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonBackReference
    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    private List<ExplorerGroup> explorerGroups;

    public Keeper(Long courseId, Long personId) {
        this.courseId = courseId;
        this.personId = personId;
    }
}
