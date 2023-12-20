package org.example.galaxy.model;


import com.fasterxml.jackson.annotation.JsonBackReference;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import javax.persistence.*;
import java.util.List;

@Entity
@Table(name = "orbit")
@Data
@ToString
public class Orbit {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long orbitId;
    @Column(nullable = false)
    private Integer orbitLevel;
    @ManyToOne(optional = false)
    @JoinColumn(name = "galaxy_id", nullable = false, insertable = false, updatable = false)
    @JsonBackReference
    private Galaxy galaxy;
    @Column(name = "galaxy_id")
    private Long galaxyId;
    @OneToMany(mappedBy = "orbit", cascade = CascadeType.ALL)
    @JsonBackReference
    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    private List<StarSystem> systems;
}
