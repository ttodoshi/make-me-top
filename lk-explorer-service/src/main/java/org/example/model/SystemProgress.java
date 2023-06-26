package org.example.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Data
@Entity
@Table(name = "system_progress")
@IdClass(SystemProgressKey.class)
@AllArgsConstructor
@NoArgsConstructor
public class SystemProgress {
    @Id
    @JoinTable(name = "person")
    @JoinColumn(name = "person_id")
    private Integer personId;
    @Id
    @JoinTable(name = "system")
    @JoinColumn(name = "system_id")
    private Integer systemId;
    private Integer progress;
}
