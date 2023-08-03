package org.example.model.feedback;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Table;

@Entity
@Table(name = "explorer_feedback", schema = "course")
@Data
public class ExplorerFeedback {
    @JoinColumn(table = "keeper", name = "keeper_id")
    private Integer keeperId;
    @Id
    @JoinColumn(table = "explorer", name = "explorer_id")
    private Integer explorerId;
    private Integer rating;
    private String comment;
}
