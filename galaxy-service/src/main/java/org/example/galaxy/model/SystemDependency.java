package org.example.galaxy.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Table(name = "system_dependency")
@Data
@NoArgsConstructor
public class SystemDependency {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer dependencyId;
    @ManyToOne(optional = false)
    @JoinColumn(name = "child_id", nullable = false, insertable = false, updatable = false)
    @JsonBackReference
    private StarSystem child;
    @Column(name = "child_id")
    private Integer childId;
    @ManyToOne(optional = false)
    @JoinColumn(name = "parent_id", insertable = false, updatable = false)
    @JsonBackReference
    private StarSystem parent;
    @Column(name = "parent_id")
    private Integer parentId;
    @Column(nullable = false)
    private Boolean isAlternative;

    public SystemDependency(Integer childId, Integer parentId, Boolean isAlternative) {
        this.childId = childId;
        this.parentId = parentId;
        this.isAlternative = isAlternative;
    }
}
