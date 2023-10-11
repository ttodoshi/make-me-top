package org.example.model;


import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.ToString;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import javax.persistence.*;
import java.util.List;

@Entity
@Table(name = "orbit")
@Data
@ToString
public class Orbit {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer orbitId;
    private Integer orbitLevel;
    private Integer systemCount;
    @ManyToOne(optional = false)
    @JoinColumn(name = "galaxy_id", nullable = false, insertable = false, updatable = false)
    @JsonBackReference
    private Galaxy galaxy;
    @Column(name = "galaxy_id")
    private Integer galaxyId;
    @OneToMany(mappedBy = "orbit", cascade = CascadeType.ALL)
    @JsonBackReference
    @ToString.Exclude
    private List<StarSystem> systems;
}
