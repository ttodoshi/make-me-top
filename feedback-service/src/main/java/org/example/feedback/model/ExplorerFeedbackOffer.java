package org.example.feedback.model;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "explorer_feedback_offer")
@Data
@NoArgsConstructor
public class ExplorerFeedbackOffer {
    @Column(nullable = false)
    private Long keeperId;
    @Id
    private Long explorerId;
    @Column(nullable = false)
    private Boolean offerValid;

    public ExplorerFeedbackOffer(Long keeperId, Long explorerId) {
        this.keeperId = keeperId;
        this.explorerId = explorerId;
        this.offerValid = true;
    }
}
