package org.example.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

@Entity
@Table(name = "person", schema = "course")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Person {
    @Id
    private Integer personId;
    private String firstName;
    private String lastName;
    private String patronymic;
    private Date registrationDate;
}
