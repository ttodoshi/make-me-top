package org.example.person.dto.feedback;

import lombok.Data;

@Data
public class ExplorerFeedbackDto {
    private Long keeperId;
    private Long explorerId;
    private Integer rating;
    private String comment;
}
