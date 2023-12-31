package org.example.galaxy.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import javax.persistence.*;
import java.util.List;

@Entity
@Table(name = "star_system")
@Data
@ToString
public class StarSystem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long systemId;
    @Column(nullable = false, unique = true)
    private String systemName;
    @Column(nullable = false)
    private Integer systemPosition;
    @ManyToOne(optional = false)
    @JoinColumn(name = "orbit_id", nullable = false, insertable = false, updatable = false)
    @JsonBackReference
    private Orbit orbit;
    @Column(name = "orbit_id")
    private Long orbitId;
    @OneToMany(mappedBy = "child", cascade = CascadeType.ALL)
    @JsonBackReference
    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    private List<SystemDependency> children;
    @OneToMany(mappedBy = "parent", cascade = CascadeType.ALL)
    @JsonBackReference
    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    private List<SystemDependency> parents;
}
