package org.example.model;

import lombok.Data;

import javax.persistence.*;

@Entity
@Table(name = "galaxy", schema = "galaxy")
@Data
public class Galaxy {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer galaxyId;
    private String galaxyName;
}
