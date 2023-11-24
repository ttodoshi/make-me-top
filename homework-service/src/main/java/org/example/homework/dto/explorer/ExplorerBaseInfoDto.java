package org.example.homework.dto.explorer;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ExplorerBaseInfoDto {
    private Integer personId;
    private String firstName;
    private String lastName;
    private String patronymic;
    private Integer explorerId;
}
