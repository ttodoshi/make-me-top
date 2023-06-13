package org.example.model;


import lombok.Data;

import javax.persistence.*;

@Table(name = "planet")
@Entity
@Data
public class PlanetDAO {
    @Id
    private Integer planetId;
    private String planetName;
    private Integer planetNumber;
    @JoinTable(name = "star_system")
    @JoinColumn(name = "system_id")
    private Integer systemId;
}
