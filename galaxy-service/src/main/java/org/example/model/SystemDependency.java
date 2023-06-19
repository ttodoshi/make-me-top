package org.example.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import lombok.ToString;

import javax.persistence.*;

@Entity
@Data
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
@ToString
public class SystemDependency {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer dependencyId;
    @ManyToOne
    @JoinColumn(name = "child_id")
    private StarSystem childId;
    @ManyToOne
    @JoinColumn(name = "parent_id")
    private StarSystem parentId;
    private Boolean isAlternative;
}
