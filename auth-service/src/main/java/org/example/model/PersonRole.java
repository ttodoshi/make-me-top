package org.example.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import lombok.Data;

import javax.persistence.*;

@Entity
@Table(name = "person_role")
@Data
@IdClass(PersonRoleId.class)
public class PersonRole {
    @Id
    private Integer personId;
    @ManyToOne(optional = false)
    @JoinColumn(name = "role_id", nullable = false, insertable = false, updatable = false)
    @JsonBackReference
    private GeneralRole role;
    @Id
    @Column(name = "role_id")
    private Integer roleId;
}
