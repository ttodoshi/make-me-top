package org.example.person.dto.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PersonCreateEvent {
    private Long personId;
    private String firstName;
    private String lastName;
    private String patronymic;
}
