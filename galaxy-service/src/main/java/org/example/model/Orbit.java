package org.example.model;


import lombok.Data;
import lombok.ToString;
import org.hibernate.annotations.Cascade;

import javax.persistence.*;

@Entity
@Table(name = "orbit", schema = "galaxy")
@Data
@ToString
public class Orbit {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer orbitId;
    private Integer orbitLevel;
    private Integer systemCount;
    @JoinColumn(table = "galaxy", name = "galaxy_id")
    @Cascade(org.hibernate.annotations.CascadeType.ALL)
    private Integer galaxyId;
}
