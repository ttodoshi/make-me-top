package org.example.person.dto.feedback;

import lombok.Data;

@Data
public class ExplorerFeedbackDto {
    private Integer keeperId;
    private Integer explorerId;
    private Integer rating;
    private String comment;
}
