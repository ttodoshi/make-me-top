package org.example.person.dto.event;

import lombok.Data;

@Data
public class PersonCreateEvent {
    private Long personId;
    private String firstName;
    private String lastName;
    private String patronymic;
}
