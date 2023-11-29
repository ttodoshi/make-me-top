package org.example.person.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import lombok.*;

import javax.persistence.*;
import java.util.List;

@Entity
@Table(name = "explorer_group")
@Data
@With
@AllArgsConstructor
@NoArgsConstructor
public class ExplorerGroup {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long groupId;
    @Column(nullable = false)
    private Long courseId;
    @ManyToOne(optional = false)
    @JoinColumn(name = "keeper_id", nullable = false, insertable = false, updatable = false)
    @JsonBackReference
    private Keeper keeper;
    @Column(name = "keeper_id")
    private Long keeperId;
    @OneToMany(mappedBy = "group", fetch = FetchType.EAGER, cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    private List<Explorer> explorers;

    public ExplorerGroup(Long courseId, Long keeperId) {
        this.courseId = courseId;
        this.keeperId = keeperId;
    }
}
