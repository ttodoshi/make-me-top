package org.example.course.dto.keeper;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class KeeperBaseInfoDto {
    private Long personId;
    private String firstName;
    private String lastName;
    private String patronymic;
    private Long keeperId;
}
