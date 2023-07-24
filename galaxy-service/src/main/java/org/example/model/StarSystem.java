package org.example.model;

import lombok.Data;
import lombok.ToString;
import org.hibernate.annotations.Cascade;

import javax.persistence.*;

@Entity
@Table(name = "star_system", schema = "galaxy")
@Data
@ToString
public class StarSystem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer systemId;
    private String systemName;
    private Integer systemLevel;
    private Integer systemPosition;
    @JoinColumn(table = "orbit", name = "orbit_id")
    @Cascade(org.hibernate.annotations.CascadeType.ALL)
    private Integer orbitId;

}
