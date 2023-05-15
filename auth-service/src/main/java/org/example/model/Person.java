package org.example.model;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Data
@Entity
@Table(name = "person")
public class Person {
    @Id
    private Integer personId;
    private String role;
    private String firstName;
    private String lastName;
    private String patronymic;

}
