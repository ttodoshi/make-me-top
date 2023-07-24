package org.example.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.*;

@Entity
@Table(name = "system_dependency", schema = "galaxy")
@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class SystemDependency {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer dependencyId;
    @ManyToOne
    @JoinColumn(name = "child_id")
    private StarSystem child;
    @ManyToOne
    @JoinColumn(name = "parent_id")
    private StarSystem parent;
    private Boolean isAlternative;
}
