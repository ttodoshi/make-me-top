package org.example.feedback.model;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "keeper_feedback_offer")
@Data
@NoArgsConstructor
public class KeeperFeedbackOffer {
    @Id
    private Long explorerId;
    @Column(nullable = false)
    private Long keeperId;
    @Column(nullable = false)
    private Boolean offerValid;

    public KeeperFeedbackOffer(Long explorerId, Long keeperId) {
        this.explorerId = explorerId;
        this.keeperId = keeperId;
        this.offerValid = true;
    }
}
