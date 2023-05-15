package org.example.model.modelDAO;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;

@Entity
@Getter
@Setter
@ToString
@RequiredArgsConstructor
@Table(name = "orbit")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler", "galactic"})
public class Orbit {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Integer orbitId;
    public Integer orbitLevel;
    public Integer countSystem;
    @JoinColumn(name = "galaxy_id")
    @JoinTable(name = "galaxy")
    private Integer galaxyId;
}
