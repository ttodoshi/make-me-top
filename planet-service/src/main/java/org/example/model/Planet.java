package org.example.model;


import lombok.Data;

import javax.persistence.*;

@Entity
@Table(name = "planet")
@Data
public class Planet {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer planetId;
    private String planetName;
    private Integer planetNumber;
    private Integer systemId;
}
