package org.example.dto.explorer;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ExplorerDTO {
    private Integer personId;
    private String firstName;
    private String lastName;
    private String patronymic;
    private Integer explorerId;
}
