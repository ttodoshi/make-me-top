package org.example.model;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;

import javax.persistence.*;

@Entity
@Table(name = "orbit")
@Data
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler", "galactic"})
@ToString
public class Orbit {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer orbitId;
    private Integer orbitLevel;
    private Integer systemCount;
    @JoinColumn(name = "galaxy_id")
    @JoinTable(name = "galaxy")
    private Integer galaxyId;
}
