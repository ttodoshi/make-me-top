package org.example.feedback.dto.event;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ExplorerFeedbackOfferCreateEvent {
    private Long keeperId;
    private Long explorerId;
}
