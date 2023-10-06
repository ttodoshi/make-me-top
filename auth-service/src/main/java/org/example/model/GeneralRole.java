package org.example.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import lombok.Data;
import lombok.ToString;

import javax.persistence.*;
import java.util.List;

@Entity
@Table(name = "role")
@Data
public class GeneralRole {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer roleId;
    @Enumerated(EnumType.STRING)
    private GeneralRoleType name;
    @OneToMany(mappedBy = "role", cascade = CascadeType.ALL)
    @JsonBackReference
    @ToString.Exclude
    private List<PersonRole> personRoles;
}
