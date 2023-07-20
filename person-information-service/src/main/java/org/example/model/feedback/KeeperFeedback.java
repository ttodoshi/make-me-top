package org.example.model.feedback;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Table;

@Entity
@Table(name = "keeper_feedback", schema = "course")
@Data
public class KeeperFeedback {
    @Id
    @JoinColumn(table = "explorer", name = "explorer_id")
    private Integer explorerId;
    @JoinColumn(table = "keeper", name = "keeper_id")
    private Integer keeperId;
    private Integer rating;
    private String comment;
}
