package org.example.model.feedback;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Table;

@Entity
@Table(name = "keeper_feedback", schema = "course")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class KeeperFeedback {
    @Id
    @JoinColumn(table = "explorer", name = "explorer_id")
    private Integer explorerId;
    @JoinColumn(table = "keeper", name = "keeper_id")
    private Integer keeperId;
    private Integer rating;
    private String comment;
}
