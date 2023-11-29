package org.example.feedback.model;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "keeper_feedback")
@Data
@NoArgsConstructor
public class KeeperFeedback {
    @Id
    private Long explorerId;
    @Column(nullable = false)
    private Long keeperId;
    @Column(nullable = false)
    private Integer rating;
    @Column(length = 1000)
    private String comment;
}
