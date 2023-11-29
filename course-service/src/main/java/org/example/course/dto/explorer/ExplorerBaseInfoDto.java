package org.example.course.dto.explorer;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ExplorerBaseInfoDto {
    private Long personId;
    private String firstName;
    private String lastName;
    private String patronymic;
    private Long explorerId;
}
