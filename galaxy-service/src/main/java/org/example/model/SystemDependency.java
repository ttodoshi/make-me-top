package org.example.model;

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
    @ManyToOne
    @JoinColumn(name = "child_id")
    private StarSystem child;
    @ManyToOne
    @JoinColumn(name = "parent_id")
    private StarSystem parent;
    @Column(nullable = false)
    private Boolean isAlternative;

    public SystemDependency(StarSystem child, StarSystem parent, Boolean isAlternative) {
        this.child = child;
        this.parent = parent;
        this.isAlternative = isAlternative;
    }
}
