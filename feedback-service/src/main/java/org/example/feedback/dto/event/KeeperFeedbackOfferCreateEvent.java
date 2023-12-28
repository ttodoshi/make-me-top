package org.example.feedback.dto.event;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class KeeperFeedbackOfferCreateEvent {
    private Long explorerId;
    private Long keeperId;
}
