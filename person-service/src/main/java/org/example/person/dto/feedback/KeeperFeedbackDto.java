package org.example.person.dto.feedback;

import lombok.Data;

@Data
public class KeeperFeedbackDto {
    private Long explorerId;
    private Integer rating;
    private String comment;
}
