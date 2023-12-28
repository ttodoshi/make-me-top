package org.example.feedback.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import lombok.Data;

import javax.persistence.*;

@Entity
@Table(name = "explorer_feedback")
@Data
public class ExplorerFeedback {
    @Id
    @Column(name = "explorer_id")
    private Long explorerId;
    @OneToOne(optional = false)
    @JoinColumn(name = "explorer_id", nullable = false, insertable = false, updatable = false)
    @JsonBackReference
    private ExplorerFeedbackOffer explorerFeedbackOffer;
    @Column(nullable = false)
    private Integer rating;
    @Column(length = 1000)
    private String comment;
}
