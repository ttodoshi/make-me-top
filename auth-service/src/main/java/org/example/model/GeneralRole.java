package org.example.model;

import lombok.Data;

import javax.persistence.*;

@Entity
@Table(name = "role", schema = "course")
@Data
public class GeneralRole {
    @Id
    private Integer roleId;
    @Enumerated(EnumType.STRING)
    private GeneralRoleType name;
}
