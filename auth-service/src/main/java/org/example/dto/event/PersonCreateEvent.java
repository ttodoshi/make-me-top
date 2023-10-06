package org.example.dto.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PersonCreateEvent {
    private Integer personId;
    private String firstName;
    private String lastName;
    private String patronymic;
}