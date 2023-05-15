package org.example.model.modelDAO;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.Cascade;

import javax.persistence.*;

@Entity
@Getter
@Setter
@ToString
@RequiredArgsConstructor
@Table(name = "star_system")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class StarSystem {
    @Id
    private Integer systemId;
    private String systemName;
    private Integer systemLevel;
    private Integer positionSystem;

    @JoinColumn(name = "orbit_id")
    @Cascade(org.hibernate.annotations.CascadeType.ALL)
    private Integer orbitId;

}
