package org.example.person.dto.feedback.offer;

import lombok.Data;

@Data
public class KeeperFeedbackOfferDto {
    private Long explorerId;
    private Long keeperId;
    private Boolean offerValid;
}
