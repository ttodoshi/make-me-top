package org.example.homework.dto.keeper;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class KeeperBaseInfoDto {
    private Integer personId;
    private String firstName;
    private String lastName;
    private String patronymic;
    private Integer keeperId;
}
