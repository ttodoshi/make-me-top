package org.example.person.dto.feedback;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class KeeperCommentDto {
    private Long personId;
    private String firstName;
    private String lastName;
    private String patronymic;
    private Long keeperId;
    private Long courseId;
    private String courseTitle;
    private Integer rating;
    private String comment;
}
