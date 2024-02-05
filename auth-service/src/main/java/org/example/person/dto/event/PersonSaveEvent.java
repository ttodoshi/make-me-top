package org.example.person.dto.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
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
