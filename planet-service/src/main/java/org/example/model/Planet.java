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
    @Column(nullable = false)
    private String planetName;
    @Column(nullable = false)
    private Integer planetNumber;
    @Column(nullable = false)
    private Integer systemId;
}
