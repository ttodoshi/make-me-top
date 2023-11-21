package org.example.galaxy.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import javax.persistence.*;
import java.util.List;

@Entity
@Table(name = "galaxy")
@Data
public class Galaxy {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer galaxyId;
    @Column(nullable = false, unique = true)
    private String galaxyName;
    @Column(columnDefinition = "TEXT", nullable = false)
    private String galaxyDescription;
    @OneToMany(mappedBy = "galaxy", cascade = CascadeType.ALL)
    @JsonBackReference
    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    private List<Orbit> orbits;
}
