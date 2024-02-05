package org.example.person.dto.event;

import lombok.Data;

@Data
public class PersonSaveEvent {
    private Long personId;
    private String firstName;
    private String lastName;
    private String patronymic;
    private String email;
    private String phoneNumber;
    private String skype;
    private String telegram;
    private Boolean isVisiblePrivateData;
}
