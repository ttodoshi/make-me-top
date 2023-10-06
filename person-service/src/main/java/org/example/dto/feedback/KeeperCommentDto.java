package org.example.dto.feedback;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class KeeperCommentDto {
    private Integer personId;
    private String firstName;
    private String lastName;
    private String patronymic;
    private Integer keeperId;
    private Integer courseId;
    private String courseTitle;
    private Integer rating;
    private String comment;
}
