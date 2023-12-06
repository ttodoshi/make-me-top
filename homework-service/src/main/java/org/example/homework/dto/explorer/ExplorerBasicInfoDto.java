package org.example.homework.dto.explorer;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ExplorerBasicInfoDto {
    private Long personId;
    private String firstName;
    private String lastName;
    private String patronymic;
    private Long explorerId;
    private Long courseId;
    private Long groupId;
}
