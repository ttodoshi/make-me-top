package org.example.person.dto.explorer;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ExplorerRequestDto {
    private Long personId;
    private String firstName;
    private String lastName;
    private String patronymic;
    private Long courseId;
    private String courseTitle;
}
