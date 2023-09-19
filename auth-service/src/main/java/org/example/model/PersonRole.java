package org.example.model;

import lombok.Data;

import javax.persistence.*;

@Entity
@Table(name = "person_role")
@Data
@IdClass(PersonRoleId.class)
public class PersonRole {
    @Id
    private Integer personId;
    @Id
    @JoinColumn(table = "role", name = "role_id")
    private Integer roleId;
}
