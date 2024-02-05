package org.example.person.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
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
@JsonInclude(JsonInclude.Include.NON_NULL)
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
    @Column(nullable = false)
    private String email;
    private String phoneNumber;
    private String skype;
    private String telegram;
    @Column(nullable = false)
    private Boolean isVisiblePrivateData;

    public Person(Long personId, String firstName, String lastName, String patronymic, String email, String phoneNumber, String skype, String telegram, Boolean isVisiblePrivateData) {
        this.personId = personId;
        this.firstName = firstName;
        this.lastName = lastName;
        this.patronymic = patronymic;
        this.maxExplorers = 0;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.skype = skype;
        this.telegram = telegram;
        this.isVisiblePrivateData = isVisiblePrivateData;
    }
}
