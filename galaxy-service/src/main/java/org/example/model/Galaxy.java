package org.example.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;
import org.hibernate.annotations.Cascade;

import javax.persistence.*;

;

@Entity
@Table(name = "galaxy")
@Data
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Galaxy {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Cascade(org.hibernate.annotations.CascadeType.ALL)
    private Integer galaxyId;
    private String galaxyName;
}
