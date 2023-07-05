package org.example.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ExplorerRequest {
    private Integer personId;
    private String firstName;
    private String lastName;
    private String patronymic;
    private Integer courseId;
    private String courseTitle;
}
