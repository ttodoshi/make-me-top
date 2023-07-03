package org.example.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.Date;
import java.util.Set;

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
