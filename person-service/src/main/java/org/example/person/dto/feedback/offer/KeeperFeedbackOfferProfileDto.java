package org.example.person.dto.feedback.offer;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class KeeperFeedbackOfferProfileDto {
    private Long keeperId;
    private Long explorerId;
    private Long personId;
    private String firstName;
    private String lastName;
    private String patronymic;
    private Long courseId;
    private String courseTitle;
}
