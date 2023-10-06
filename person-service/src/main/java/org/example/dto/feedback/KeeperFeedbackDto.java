package org.example.dto.feedback;

import lombok.Data;

@Data
public class KeeperFeedbackDto {
    private Integer explorerId;
    private Integer keeperId;
    private Integer rating;
    private String comment;
}
