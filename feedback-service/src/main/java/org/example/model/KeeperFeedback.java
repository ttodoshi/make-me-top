package org.example.model;

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
    private Integer explorerId;
    private Integer keeperId;
    private Integer rating;
    @Column(length = 1000)
    private String comment;
}
