package org.example.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Table(name = "system_dependency", schema = "galaxy")
@Data
@AllArgsConstructor
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
    private Boolean isAlternative;
}
