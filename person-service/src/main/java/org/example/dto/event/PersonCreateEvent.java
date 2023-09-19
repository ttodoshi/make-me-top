package org.example.dto.event;

import lombok.Data;

@Data
public class PersonCreateEvent {
    private Integer personId;
    private String firstName;
    private String lastName;
    private String patronymic;
}
