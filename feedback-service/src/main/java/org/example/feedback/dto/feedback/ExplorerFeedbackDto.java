package org.example.feedback.dto.feedback;

import lombok.Data;

@Data
public class ExplorerFeedbackDto {
    private Long explorerId;
    private Integer rating;
    private String comment;
}
