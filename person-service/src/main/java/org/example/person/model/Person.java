package org.example.person.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "person")
@Data
@NoArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class Person {
    @Id
    private Long personId;
    @Column(nullable = false)
    private String firstName;
    @Column(nullable = false)
    private String lastName;
    private String patronymic;
    @CreatedDate
    @Column(nullable = false)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy HH:mm:ss")
    private LocalDateTime registrationDate;
    @Column(nullable = false)
    private Integer maxExplorers;

    public Person(Long personId, String firstName, String lastName, String patronymic) {
        this.personId = personId;
        this.firstName = firstName;
        this.lastName = lastName;
        this.patronymic = patronymic;
        this.maxExplorers = 0;
    }
}
