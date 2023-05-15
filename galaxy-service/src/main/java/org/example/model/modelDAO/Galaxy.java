package org.example.model.modelDAO;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;
import org.hibernate.annotations.Cascade;

import javax.persistence.*;

;

@Entity
@Table(name = "galaxy")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
@Data
public class Galaxy {
    @Id
    @Cascade(org.hibernate.annotations.CascadeType.ALL)
    private Integer galaxyId;
    private String galaxyName;
}
