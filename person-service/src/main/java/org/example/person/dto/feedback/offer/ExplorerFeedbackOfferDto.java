package org.example.person.dto.feedback.offer;

import lombok.Data;

@Data
public class ExplorerFeedbackOfferDto {
    private Long keeperId;
    private Long explorerId;
    private Boolean offerValid;
}
